package meldev.views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import meldev.Activator;
import meldev.editors.MelEditor;
import meldev.preferences.PreferenceConstants;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;

//! Sends currently selected text to Maya over command port
public class EditorSendToMayaDelegate extends ActionDelegate implements IEditorActionDelegate {
	private MelEditor fTargetEditor;

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MelEditor) {
			fTargetEditor = (MelEditor)targetEditor;
		}
	}

	@Override
	public void run(IAction action) {
		final int kSocketReadTimeoutMS = 1000; 
		TextSelection sel = (TextSelection)fTargetEditor.getSelectionProvider().getSelection();
		String cmd = sel.getText();
		if (cmd.length() > 0) {
			try {
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				int port = store.getInt(PreferenceConstants.P_COMMANDPORT_PORT);
				String host = store.getString(PreferenceConstants.P_COMMANDPORT_HOST);
				Socket sock = new Socket(host, port); 
				sock.setSoTimeout(kSocketReadTimeoutMS);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				writer.write(cmd);
				writer.write(0);
				System.out.print("Wrote text to " + host + ":" + port + "\n");
				try {
					String ln = reader.readLine();
					while (ln != null) {
						System.out.print("Recieved:" + ln + "\n");
						ln = reader.readLine();
					}
				} catch (SocketTimeoutException e) {
					// no problem, just timed out on the read
				}
				writer.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

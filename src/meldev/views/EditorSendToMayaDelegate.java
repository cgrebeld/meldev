package meldev.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import meldev.editors.MelEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;

//! Sends currently selected text to Maya over command port
public class EditorSendToMayaDelegate extends ActionDelegate implements IEditorActionDelegate {

	private static final String kMayaCommandPortFile = "/tmp/commandportDefault";
	private static final String kMayaCommandPortHost = "localhost";
	private static final int kMayaCommandPortPort = 3030;
	private MelEditor fTargetEditor;

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MelEditor) {
			fTargetEditor = (MelEditor)targetEditor;
		}
	}

	@Override
	public void run(IAction action) {
		TextSelection sel = (TextSelection)fTargetEditor.getSelectionProvider().getSelection();
		String cmd = sel.getText();
		if (cmd.length() > 0) {
			try {
				Socket sock = new Socket(kMayaCommandPortHost, kMayaCommandPortPort); 
				BufferedWriter of = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				of.write(cmd);
				of.write(0);
				of.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

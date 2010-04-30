package meldev.views;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import meldev.editors.MelEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class EditorFindReferencesDelegate extends ActionDelegate implements
		IEditorActionDelegate {
	private MelEditor fTargetEditor;
	private HashMap<String,IFile> fIdentifierHash;
	
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MelEditor) {
			fTargetEditor = (MelEditor)targetEditor;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		TextSelection sel = (TextSelection)fTargetEditor.getSelectionProvider().getSelection();
		String cmd = sel.getText();
		if (cmd.length() > 0) {
			
			ArrayList<IFile> files = MelProjectFiles.get();
			System.err.print("run with "  + files.size() + "\n");
			IDocumentProvider provider = fTargetEditor.getDocumentProvider();
			for(IFile file : files) {
				//try {
					//IDocument doc = provider.getDocument(file);
					//String msg = file.getName() + " " + String.valueOf(doc.getNumberOfLines()) + "\n";
					String msg = file.getName() + " " +"\n";
					System.err.print(msg);
					//InputStreamReader rdr = new InputStreamReader(file.getContents(true));
					//rdr.read
//				} catch (CoreException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionDelegate#init(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void init(IAction action) {
		System.err.print("HI THERE\n");
	}

}

package meldev.views;

import java.io.File;

import meldev.Activator;
import meldev.editors.MelEditor;
import meldev.editors.MelTag;
import meldev.preferences.PreferenceConstants;
import meldev.util.MelCTag;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.ide.IDE;

//! Sends currently selected text to Maya over command port
public class EditorFindDeclarationDelegate extends ActionDelegate implements IEditorActionDelegate {
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
		String symbol = sel.getText();
		if (symbol.length() > 0) {
			IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (win == null)
				return;
			IWorkbenchPage page = win.getActivePage();
			if (page == null)
				return;
			
			boolean foundIt = false;
			// Try to find definition in tags file
			if (MelEditor.getTagsFileManager().hasTags()) {
				MelCTag tag = MelEditor.getTagsFileManager().find(symbol);
				if (tag != null) {
					System.out.print("Found tag: " + tag.file + ":" +tag.line.toString() + "\n");
					IFile file = MelProjectFiles.findFile(tag.file);
					IEditorPart newPart = null;
					if (file == null || ! file.isAccessible()) {
						// Hmm, not in an open project.  Try opening from filesystem
						String prefix = Activator.getDefault().getPreferenceStore().
							getString(PreferenceConstants.P_TAGSFILE_ROOT);
						File extFile = new File(prefix + "/" + tag.file);
						if (extFile.canRead()) {
							IFileStore fileStore = EFS.getLocalFileSystem().getStore(extFile.toURI());
							try {
								newPart = IDE.openEditorOnFileStore( page, fileStore );
							} catch ( PartInitException e ) {
								e.printStackTrace();
							}
						} else {
							// couldn't resolve file suffix to real file
							System.out.print("Couldn't open " + extFile.getAbsolutePath() + "\n");
						}
					} else {
						try {
							newPart = IDE.openEditor(page, file);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
					}
					if (null != newPart && newPart instanceof MelEditor) {
						int offset, len;
						MelEditor newEd = (MelEditor)newPart;
						IDocument doc = newEd.getDocumentProvider().getDocument(newEd.getEditorInput());
						if (doc != null) {
							try {
								IRegion info = doc.getLineInformation(tag.line);
								offset = info.getOffset();
								len = info.getLength();
								newEd.setHighlightRange(offset, len, true);
								foundIt = true;
							} catch (BadLocationException e) {
								System.err.print("Your tags file seems incorrect!");
							}
						}
					}
				}
			} 
			if (!foundIt) {
				// Not in tags file.  Try to find it in open buffers
				//System.out.print("Failed to find in tags file, looking at viewed buffers.\n");				
				for(MelDocumentTagsContentProvider provider : MelDocumentTagsContentProvider.getProviderInstances()) {
					for (Object tago: provider.getElements(null)) {
						MelTag tag = (MelTag)tago;
						//System.out.print("looking at tag " + tag.tag + "\n");
						if (tag.tag.equals(symbol)) {
							// sweet
							//System.out.print("Found local tag " + tag.tag + "\n");
							page.activate(provider.getEditor());
							provider.getEditor().setHighlightRange(tag.offset, tag.length, true);
						}
					}
				}

			}
		}
	}
}

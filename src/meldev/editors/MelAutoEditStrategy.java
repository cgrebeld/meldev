package meldev.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class MelAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	private String fIndentString;

	public MelAutoEditStrategy(String[] indentPrefixes) {
		super();
		StringBuffer buf= new StringBuffer();
		buf.append(indentPrefixes[0]);
		fIndentString = buf.toString();
	}
	
	@Override
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		// indent up to previous line's level
		//System.out.print(c.owner + ": ");
		boolean doit =	(c.length == 0 && c.text != null && 
				TextUtilities.endsWith(d.getLegalLineDelimiters(), c.text) != -1);
		if (doit) {
			super.customizeDocumentCommand(d, c);
			// entering a newline, but does the previous line end with open-brace?
			if (c.offset == -1 || d.getLength() == 0)
				return;
			try {
				int p= (c.offset == d.getLength() ? c.offset  - 1 : c.offset);
				if (p > 0 ) {
					char ch = d.getChar(p - 1);
					if (ch == '{') {
						StringBuffer buf= new StringBuffer(c.text);
						buf.append(fIndentString);
						// buf.append(c.text);
						// buf.append('}');
						c.text = buf.toString();
						// moveCursorToOffset(d, c.offset + fIndentString.length());
					}
				}
			} catch (BadLocationException excp) {
				// stop work
			}
		}
	}
/*
	private void moveCursorToOffset(IDocument d, int offset) {
		IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorReference editorReferences[]= window.getActivePage().getEditorReferences();

		for (int i= 0; i < editorReferences.length; i++) {
			IEditorPart editor= editorReferences[i].getEditor(false); // don't create!
			if (editor instanceof MelEditor) {
				ITextEditor textEditor= (ITextEditor) editor;
				IEditorInput input= textEditor.getEditorInput();
				IDocument doc= textEditor.getDocumentProvider().getDocument(input);
				if (d.equals(doc)) {
					// found our editor.. jeezus there MUST be a simpler way
					// TODO: Doesn't change anything
					textEditor.selectAndReveal(offset, 1);
				}
			}
		}
	}
*/
}

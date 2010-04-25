package meldev.views;

import meldev.Activator;
import meldev.editors.MelEditor;
import meldev.editors.MelTag;
import meldev.views.MelDocumentTagsContentProvider.ITagsChangeListener;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class MelContentOutlinePage extends ContentOutlinePage implements ITagsChangeListener {
	private static Image fsMethodImage;
	private static Image fsVarImage;
	private IEditorInput fInput;
	private IDocumentProvider fDocumentProvider;
	private MelEditor fEditor;
	private MelDocumentTagsContentProvider fTagsProvider;
	private boolean fInSelection = false;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if ((selection instanceof ITreeSelection) && ! fInSelection) {
			MelTag obj = (MelTag)((ITreeSelection)selection).getFirstElement();
			if (null != obj) {
				fEditor.setHighlightRange(obj.offset, obj.length, true);
			}
		} else if ((selection instanceof ITextSelection) && ! fInSelection){
			// editor selection changed
			// TODO: Not quite working
			/*
			int offset = ((ITextSelection)selection).getOffset();
			Object [] tagAtOffsetPath = fTagsProvider.getTagAt(offset);
			if (tagAtOffsetPath != null) {
				getTreeViewer().setSelection(new TreeSelection(new TreePath(tagAtOffsetPath)));
			}
			*/
		}
		super.selectionChanged(event);
	}

	
	static {
		fsMethodImage = Activator.getImageDescriptor("/icons/method.png").createImage();
		fsVarImage = Activator.getImageDescriptor("/icons/variable.png").createImage();
	}

	class MelTagLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object elem) {
			Image img = null;
			if (elem.getClass().equals(MelTag.class)) {
				MelTag tag = (MelTag)elem;
				if (tag.type == MelTag.Type.kProcDef) {
					img = fsMethodImage;
				} else if (tag.type == MelTag.Type.kVarDef) {
					img = fsVarImage;
				}
			}
			return img;
		}
	};
	
	public MelContentOutlinePage(IDocumentProvider documentProvider,
			MelEditor melEditor) {
		super();
		fDocumentProvider = documentProvider;
		fEditor = melEditor;
		// TODO: Track cursor movement by updating selected item in outline
		// not quite working
		// fEditor.getSelectionProvider().addSelectionChangedListener(this);
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		fTagsProvider = new MelDocumentTagsContentProvider(fDocumentProvider);
		fTagsProvider.addChangeListener(this);
		viewer.setContentProvider(fTagsProvider);
		viewer.setLabelProvider(new MelTagLabelProvider());
		if (fInput != null)
			viewer.setInput(fInput);
		viewer.addSelectionChangedListener(this);
	}

	public void setInput(IEditorInput editorInput) {
		fInput = editorInput;
	}

	@Override
	public void tagsChanged() {
		getTreeViewer().refresh(false);
	}
};

package meldev.editors;

import meldev.Activator;
import meldev.preferences.PreferenceConstants;
import meldev.util.TagsFileManager;
import meldev.views.MelContentOutlinePage;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class MelEditor extends TextEditor {

	private IPropertyChangeListener preferenceListener;
	private MelSourceViewerConfiguration configuration;
	private MelContentOutlinePage fOutlinePage;
	static private TagsFileManager fTagsFileManager;
	
	static {
		fTagsFileManager = new TagsFileManager();
	}
	
	static public TagsFileManager getTagsFileManager() {
		return fTagsFileManager;
	}

	public MelEditor() {
		super();
		configuration = new MelSourceViewerConfiguration();
		setSourceViewerConfiguration(configuration);
		setDocumentProvider(new MelDocumentProvider());
		preferenceListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				String prop = arg0.getProperty();
				if (prop.equals(PreferenceConstants.P_SYNTAXCOLOR_COMMAND)
						|| prop
								.equals(PreferenceConstants.P_SYNTAXCOLOR_COMMENT)
						|| prop
								.equals(PreferenceConstants.P_SYNTAXCOLOR_DEFAULT)
						|| prop
								.equals(PreferenceConstants.P_SYNTAXCOLOR_KEYWORD)
						|| prop
								.equals(PreferenceConstants.P_SYNTAXCOLOR_NUMBER)
						|| prop
								.equals(PreferenceConstants.P_SYNTAXCOLOR_STRING)
						|| prop
								.equals(PreferenceConstants.P_SYNTAXCOLOR_VARIABLE)) {

					configuration.resetMelScanner();
					// TODO: invalidate viewer, but how?
				}
			};
		};
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(
				preferenceListener);
	}

	public void dispose() {
		Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(preferenceListener);
		if(null != fOutlinePage)
			fOutlinePage.dispose();
		super.dispose();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage = new MelContentOutlinePage(getDocumentProvider(),
						this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getEditorInput());
			}
			return fOutlinePage;
		}
		return super.getAdapter(required);
	}
}

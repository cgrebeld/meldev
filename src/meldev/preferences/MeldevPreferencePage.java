package meldev.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import meldev.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class MeldevPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public MeldevPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Syntax Color and Misc. Preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		// Syntax colors
		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_STRING, "Strings", getFieldEditorParent()));
		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_DEFAULT, "Default", getFieldEditorParent()));
		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_COMMAND, "Commands", getFieldEditorParent()));
		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_COMMENT, "Comments", getFieldEditorParent()));
		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_KEYWORD, "Keywords", getFieldEditorParent()));

		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_NUMBER, "Numbers", getFieldEditorParent()));
		addField(
				new ColorFieldEditor(PreferenceConstants.P_SYNTAXCOLOR_VARIABLE, "Variables", getFieldEditorParent()));
		addField(
				new IntegerFieldEditor(PreferenceConstants.P_COMMANDPORT_PORT, "Maya Command Port Number", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_COMMANDPORT_HOST, "Maya Command Port Host", getFieldEditorParent()));
		addField(
				new FileFieldEditor(PreferenceConstants.P_TAGSFILE_PATH, "CTAGS File", getFieldEditorParent()));
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}
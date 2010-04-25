package meldev.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;

import meldev.Activator;
import meldev.editors.IMelSyntaxColors;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_COMMAND,
				IMelSyntaxColors.COMMAND);
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_COMMENT,
				IMelSyntaxColors.COMMENT);
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_DEFAULT,
				IMelSyntaxColors.DEFAULT);
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_KEYWORD,
				IMelSyntaxColors.KEYWORD);
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_NUMBER,
				IMelSyntaxColors.NUMBER);
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_STRING,
				IMelSyntaxColors.STRING);
		PreferenceConverter.setDefault(store, 
				PreferenceConstants.P_SYNTAXCOLOR_VARIABLE,
				IMelSyntaxColors.VARIABLE);
		store.setDefault(
				PreferenceConstants.P_COMMANDPORT_PORT,
				65001);
	}
}

package meldev.util;


import java.io.File;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import meldev.Activator;
import meldev.preferences.PreferenceConstants;
import meldev.util.Trie.Node;

//! Handles loading tags from disk and searching the same
public class TagsFileManager {
	private Trie tags;
	private IPropertyChangeListener preferenceListener;
	
	public TagsFileManager() {
		preferenceListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				String prop = arg0.getProperty();
				if (prop.equals(PreferenceConstants.P_TAGSFILE_PATH)){
					load();
				}
			}
		};
		Activator.getDefault().getPreferenceStore().
			addPropertyChangeListener(preferenceListener);
		
		load();
	}
	
	private void load() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String tagsfile = store.getString(PreferenceConstants.P_TAGSFILE_PATH);
		if (null == tagsfile)
			return;
		File f = new File(tagsfile);
		if (f.canRead()) {
			tags = new Trie();
			TagReaderJob job = new TagReaderJob(tags, f);
			job.schedule();
		}
	}
	
	public boolean hasTags() {
		return tags != null;
	}
	
	public MelCTag find(String symbol) {
		Node res = tags.find(symbol);
		if (res != null && res.getData() != null) {
			MelCTag tag = (MelCTag)res.getData();
			return tag;
		}
		return null;
	}
}

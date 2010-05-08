/**
 * 
 */
package meldev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import meldev.Activator;
import meldev.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Read MEL tags from a ctags file into a Trie of tag objects
 *
 */
public class TagReaderJob extends Job {
	private Trie fTags;
	private File fTagsFile;

	public TagReaderJob(Trie tags, File tagsfile) {
		super("Loading Tags from disk job");
		fTags = tags;
		fTagsFile = tagsfile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		int count = 0;

		try {
			BufferedReader input = new BufferedReader(new FileReader(fTagsFile));
			String line = null;
			final int totaltags = 36000;
			monitor.beginTask("Reading from tags file " + fTagsFile.getAbsolutePath(), totaltags);
			
			while ((line = input.readLine()) != null) {
				String[] lineParts = line.split("\t");
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;

				if (lineParts.length == 3) {
					String symbol = lineParts[0];
					String file = lineParts[1];
					Integer lineno = Integer.valueOf(lineParts[2]);
					if (symbol.length() > 1 && line != null && file != null &&
							lineParts[1].endsWith(".mel"))
					{
						monitor.worked(1);
						++count; 
						MelCTag tag = new MelCTag();
						tag.file = file.replace("$TAGSRC/","");
						tag.line = lineno;
						tag.type = symbol.startsWith("$") ? MelCTag.Type.kVarDef : MelCTag.Type.kProcDef;
						fTags.addWord(symbol, tag);
					}
				}
			}
			System.out.print("Read " + String.valueOf(count) + " tags from file.\n");
		} catch (FileNotFoundException e) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Tags file " + fTagsFile.getAbsolutePath() + " not found.");
		} catch (IOException e) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Error while parsing Tags file " + fTagsFile.getAbsolutePath());
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}

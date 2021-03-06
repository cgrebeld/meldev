package meldev.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import meldev.Activator;
import meldev.util.TagsFileManager;
import meldev.util.Trie;
import meldev.util.Trie.Word;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.texteditor.HippieProposalProcessor;

public class MelContentAssistProcessor implements
		org.eclipse.jface.text.contentassist.IContentAssistProcessor {

	private static final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];
	private static final ICompletionProposal[] NO_PROPOSALS= new ICompletionProposal[0];
	
	private static HippieProposalProcessor fHippieProcessor;
	private static Trie fCommandTrie;
	
	static {
		fHippieProcessor = new HippieProposalProcessor();
		fCommandTrie = new Trie();
		loadCommandList();
	}
	
	
	public MelContentAssistProcessor() { }
	
	
	private static void loadCommandList() {
		// read in the known commands and insert into the trie
		try {
			File f = new File(FileLocator.toFileURL(
					Activator.getDefault().getBundle().getEntry(
							"/data/commandList")).toURI());
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine()) != null) {
				String cmd = line.substring(0,line.indexOf(" "));
				fCommandTrie.addWord(cmd, null);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		ICompletionProposal[] proposals = fHippieProcessor.computeCompletionProposals(viewer, offset);

		ArrayList<ICompletionProposal> allprops = new ArrayList<ICompletionProposal>(proposals.length);
		for(ICompletionProposal p : proposals) {
			allprops.add(p);
		}

		// Look up the current token in the tags storage
		String prefix;
		try {
			prefix = getPrefix(viewer, offset);
			if (prefix == null || prefix.length() == 0)
				return NO_PROPOSALS;
			getTagsProposals(prefix, offset - prefix.length(), allprops);
			getCommandProposals(prefix, offset - prefix.length(), allprops);
		} catch (BadLocationException e) {
			return NO_PROPOSALS;
		}
		return allprops.toArray(proposals);
	}

	//! get proposals based on the tags file
	private void getTagsProposals(String prefix, int offset, ArrayList<ICompletionProposal> proposals) {
		// add in ctags completions if we have them
		if (MelEditor.getTagsFileManager().hasTags()) {
			ArrayList<Word> tagscompletions = MelEditor.getTagsFileManager().getCompletions(prefix);
			for (Word word : tagscompletions) {
				proposals.add(new CompletionProposal(word.fWord, offset, prefix.length(),offset+word.fWord.length()));
			}
		}
	}
	
	//! get proposals based on maya command list
	private void getCommandProposals(String prefix, int offset, ArrayList<ICompletionProposal> proposals) {
		// add in command completions
		ArrayList<Word> completions = fCommandTrie.getCompletions(prefix);
		for (Word word : completions) {
			proposals.add(new CompletionProposal(word.fWord, offset, prefix.length(),offset+word.fWord.length()));
		}
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return NO_CONTEXTS;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		// no custom error message
		return null;
	}
	private String getPrefix(ITextViewer viewer, int offset) throws BadLocationException {
		IDocument doc= viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;

		int length= 0;
		while (--offset >= 0 && Character.isJavaIdentifierPart(doc.getChar(offset)))
			length++;

		return doc.get(offset + 1, length);
	}


}

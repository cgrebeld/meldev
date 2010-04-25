package meldev.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.*;

public class MelPartitionScanner extends RuleBasedPartitionScanner {
	public final static String MULTILINE_COMMENT = "__mel_multiline_comment";
	public static final String SINGLELINE_COMMENT = "__mel_singleline_comment";
	public static final String STRING = "__mel_string";
	public static final String[] fContentTypes = new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			MULTILINE_COMMENT,SINGLELINE_COMMENT, STRING };

	public MelPartitionScanner() {
		super();
		ArrayList<IRule> rules= new ArrayList<IRule>();
		
		IToken multilinecomment = new Token(MULTILINE_COMMENT);
		IToken string = new Token(STRING);
		IToken singlelinecomment = new Token(SINGLELINE_COMMENT);
		
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", singlelinecomment)); 

		// Add rule for strings 
		rules.add(new SingleLineRule("\"", "\"", string, '\\', false, false)); 

		// Add rules for multi-line comments
		rules.add(new MultiLineRule("/*", "*/", multilinecomment, (char) 0, true)); 

		IPredicateRule[] result= new IPredicateRule[rules.size()];
		
		rules.toArray(result);
		
		setPredicateRules(result);
	}
}

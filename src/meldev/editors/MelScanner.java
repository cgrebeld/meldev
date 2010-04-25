package meldev.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import meldev.Activator;
import meldev.preferences.PreferenceConstants;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class MelScanner extends RuleBasedScanner {

	public MelScanner() {
		reset();
	}

	public void reset() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		IToken command =
			new Token(
				new TextAttribute(new Color(Display.getDefault(), 
						PreferenceConverter.getColor(store,PreferenceConstants.P_SYNTAXCOLOR_COMMAND)))
				);
		IToken keyword =
			new Token(
				new TextAttribute(new Color(Display.getDefault(), 
						PreferenceConverter.getColor(store,PreferenceConstants.P_SYNTAXCOLOR_KEYWORD)))
				);
		IToken number =
			new Token(
				new TextAttribute(new Color(Display.getDefault(), 
						PreferenceConverter.getColor(store,PreferenceConstants.P_SYNTAXCOLOR_NUMBER)))
				);
		IToken variable =
			new Token(
				new TextAttribute(new Color(Display.getDefault(), 
						PreferenceConverter.getColor(store,PreferenceConstants.P_SYNTAXCOLOR_VARIABLE)))
				);
/*		
		IToken procDef =
			new Token(
				new TextAttribute(new Color(Display.getDefault(), 
						PreferenceConverter.getColor(store,PreferenceConstants.P_SYNTAXCOLOR_DEFAULT)))
				);
*/		
		ArrayList<IRule> rules = new ArrayList<IRule>(4);
		
		// Add generic whitespace rule.
		//rules.add(new WhitespaceRule(new MelWhitespaceDetector()));
		// Number rule
		rules.add(new NumberRule(number));
		// variable rule
		rules.add(new WordPatternRule(new MelIdentifierDetector(), "$", null, variable));
		// keyword rule
		WordRule identRule = new IdentifierRule(new MelIdentifierDetector());
		String[] kwds = new String[] {         
			"default",
	        "switch",
	        "case",
	        "do",
	        "for",
	        "in",
	        "while",
	        "if",
	        "else",
	        "break",
	        "continue",
	        "return",
	        "source",
	        "catch",
	        "alias",
	        "yes",
	        "true",
	        "on",
	        "no",
	        "false",
	        "off",
	        "int",
	        "float",
	        "vector",
	        "void",
	        "string",
	        "matrix",
	        "global",
	        "proc",
	        "catchQuiet",
	        "`"};
		for(String kwd : kwds) {
			identRule.addWord(kwd, keyword);
		}
		
		// command rule
		try {
			File f = new File(FileLocator.toFileURL(
					Activator.getDefault().getBundle().getEntry(
							"/data/commandList")).toURI());
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine()) != null) {
				String cmd = line.substring(0,line.indexOf(" "));
				identRule.addWord(cmd, command);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rules.add(identRule);
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}

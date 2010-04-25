package meldev.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class MelWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}

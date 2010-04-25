package meldev.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class IdentifierRule extends WordRule {

	/** Buffer used for pattern detection, hides WordRule::fBuffer */
	private StringBuffer fBuffer= new StringBuffer();

	public IdentifierRule(IWordDetector detector) {
		super(detector);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.WordRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		if (c != ICharacterScanner.EOF && 
				((!Character.isLetterOrDigit(c)) || scanner.getColumn() == 1 )) {
			boolean extraread = true;
			if (scanner.getColumn() == 1) {
				scanner.unread();
				extraread = false;
			}
			c = scanner.read();
			if (c != ICharacterScanner.EOF && fDetector.isWordStart((char) c)) {
				if (fColumn == UNDEFINED
						|| (fColumn == scanner.getColumn() - 1)) {
					fBuffer.setLength(0);
					do {
						fBuffer.append((char) c);
						c = scanner.read();
					} while (c != ICharacterScanner.EOF
							&& fDetector.isWordPart((char) c));
					//boolean ok = c == ICharacterScanner.EOF || Character.isWhitespace(c);
					scanner.unread();
					String buffer = fBuffer.toString();
					IToken token = (IToken) fWords.get(buffer);
					if (token != null) {
						return token;
					}
					if (fDefaultToken.isUndefined())
						unreadBuffer(scanner);
					return fDefaultToken;
				}
			}
			if (extraread)
				scanner.unread();
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

	@Override
	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i= fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}

}

package meldev.editors;

import org.eclipse.swt.graphics.RGB;

public interface IMelSyntaxColors {
	RGB COMMENT = new RGB(128, 0, 0);
	RGB STRING = new RGB(0, 128, 0);
	RGB DEFAULT = new RGB(0, 0, 0);
	RGB NUMBER = new RGB(128,128,0);
	RGB VARIABLE = new RGB(255,128,64);
	RGB KEYWORD = new RGB(128,0,255);
	RGB COMMAND = new RGB(64,64,255);
}

package meldev.util;

public class MelCTag implements Comparable<MelCTag> {
	public enum Type {
		kProcDef,
		kVarDef
	}
	public Integer line;
	public Type type;
	public String file;
	@Override
	public int compareTo(MelCTag arg0) {
		return line.compareTo(arg0.line);
	}
}


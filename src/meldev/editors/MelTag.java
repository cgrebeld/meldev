package meldev.editors;

public class MelTag implements Comparable<MelTag> {
	public enum Type {
		kProcDef,
		kVarDef
	}
	public Integer offset;
	public int length;
	public String tag;
	public Type type;
	public boolean isGlobal;
	public String toString(){
		return tag;
	}
	@Override
	public int compareTo(MelTag arg0) {
		return offset.compareTo(arg0.offset);
	}
}

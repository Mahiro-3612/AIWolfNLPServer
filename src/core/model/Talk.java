package core.model;

public class Talk {
	final static public String OVER = "Over";
	final static public String SKIP = "Skip";
	final static public String FORCE_SKIP = "ForceSkip";

	public final int idx;
	public final int day;
	public final int turn;
	public final Agent agent;
	public final String text;

	public Talk(int idx, int day, int turn, Agent agent, String text) {
		this.idx = idx;
		this.day = day;
		this.turn = turn;
		this.agent = agent;
		this.text = text;
	}

	public boolean isSkip() {
		return text.equals(SKIP);
	}

	public boolean isOver() {
		return text.equals(OVER);
	}

	@Override
	public String toString() {
		return String.format("Day%02d %02d[%03d]\t%s\t%s", day, turn, idx, agent, text);
	}
}

package cz.gattserver.grass3.language.model.dto;

public class CrosswordHintTO implements CrosswordCell {

	private int id;
	private int x;
	private int y;
	private boolean horizontally;
	private String hint;

	public CrosswordHintTO(int id, int x, int y, boolean horizontally, String hint) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.horizontally = horizontally;
		this.hint = hint;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHorizontally() {
		return horizontally;
	}

	public String getHint() {
		return hint;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String getValue() {
		return String.valueOf(id);
	}

	@Override
	public boolean isWriteAllowed() {
		return false;
	}

}

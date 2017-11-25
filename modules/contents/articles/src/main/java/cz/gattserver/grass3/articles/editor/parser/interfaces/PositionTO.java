package cz.gattserver.grass3.articles.editor.parser.interfaces;

/**
 * Pozice ve vstupnim souboru.
 */
public class PositionTO {

	private int line;
	private int col;

	public PositionTO(int line, int col) {
		this.line = line;
		this.col = col;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

}

package org.myftp.gattserver.grass3.articles.parser.interfaces;

import org.myftp.gattserver.grass3.articles.parser.Position;

public abstract class AbstractElementTree {

	private Position startPosition;

	private Position endPosition;

	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
	}

	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
	}

	/**
	 * Začne vypisovat dle stromu přeložen obsah elementu
	 * 
	 * @param ctx
	 *            kontext, do kterého se má výstup zapisovat
	 */
	public final void generate(IContext ctx) {
		// Nejprve zapíše značky pro částečné úpravy
		if (startPosition != null && endPosition != null)
			ctx.print("<a class=\"quick_edit_position_link\" href=\"#\" onclick=\"quickedit(" + startPosition.getCol()
					+ "," + startPosition.getLine() + "," + endPosition.getCol() + "," + endPosition.getLine()
					+ ");\"><img src=\"/grass/img/tags/quickedit_16.png\"/></a>");

		// Pak zavolá k provedení samotný element
		generateElement(ctx);
	}

	/**
	 * Metoda k implementování skutečným stromem elementu, je volána hlavní
	 * generující metodou, která musí nejprve zapsan odkazující značky na
	 * zdrojový text článku
	 * 
	 * @param ctx
	 */
	protected abstract void generateElement(IContext ctx);
}

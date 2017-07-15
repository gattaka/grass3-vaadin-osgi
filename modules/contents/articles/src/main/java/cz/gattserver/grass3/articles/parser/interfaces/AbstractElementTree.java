package cz.gattserver.grass3.articles.parser.interfaces;

import cz.gattserver.grass3.articles.parser.Position;

public abstract class AbstractElementTree {

	@SuppressWarnings("unused")
	private Position startPosition;

	@SuppressWarnings("unused")
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

package cz.gattserver.grass3.articles.editor.parser.elements;

import cz.gattserver.grass3.articles.editor.parser.Context;

public interface Element {

	/**
	 * Metoda k implementování skutečným stromem elementu, je volána hlavní
	 * generující metodou, která musí nejprve zapsan odkazující značky na
	 * zdrojový text článku
	 * 
	 * @param ctx
	 */
	void apply(Context ctx);
}

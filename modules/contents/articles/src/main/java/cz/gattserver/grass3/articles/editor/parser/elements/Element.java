package cz.gattserver.grass3.articles.editor.parser.elements;

import java.util.List;

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

	/**
	 * Metoda pro procházení podstromu při post-procesingu
	 * 
	 * @return list elementů, nebo <code>null</code>
	 */
	List<Element> getSubElements();
}

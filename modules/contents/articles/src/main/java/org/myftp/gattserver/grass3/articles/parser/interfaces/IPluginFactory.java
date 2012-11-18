package org.myftp.gattserver.grass3.articles.parser.interfaces;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;

/**
 * Rozhraní předepisující vlastnosti, které musí mít každý parser plugin do
 * editoru článků
 * 
 * @author gatt
 */
public interface IPluginFactory {

	/**
	 * Hlavní identifikační metoda
	 * 
	 * @return identifikátor elemetu - jeho tag, musí být unikátní mezi
	 *         ostatními elementy jinak bude při překladu docházet ke kolizím
	 */
	public String getTag();

	/**
	 * Získá instanci parseru
	 * 
	 * @return instance {@link ParserPlugin}
	 */
	public AbstractParserPlugin getPluginParser();

	/**
	 * Získá balíček s tagem, nápisy a ikonou pro UI editoru
	 * 
	 * @return balíček UI - viz, {@link EditorButtonResources}
	 */
	public EditorButtonResources getEditorButtonResources();

	/**
	 * Získá název rodiny elementů, pod kterou má být seskupen plugin v editoru
	 * 
	 * @return název rodiny pluginů - např. LaTeX, HTML, FancyNadpisy apod.
	 */
	public String getTagFamily();

}

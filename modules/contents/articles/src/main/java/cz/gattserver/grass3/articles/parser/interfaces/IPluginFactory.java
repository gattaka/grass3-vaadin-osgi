package cz.gattserver.grass3.articles.parser.interfaces;

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

}

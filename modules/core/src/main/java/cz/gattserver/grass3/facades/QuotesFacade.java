package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.interfaces.QuoteTO;

public interface QuotesFacade {

	/**
	 * Uloží novou hlášku
	 * 
	 * @param content
	 *            obsah hlášky
	 * @return id hlášky
	 */
	public long createQuote(String content);

	/**
	 * Upraví existující hlášku
	 * 
	 * @param quoteId
	 *            id existující hlášky
	 * @param modifiedContent
	 *            upravený obsah
	 */
	public void modifyQuote(long quoteId, String modifiedContent);

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteTO}
	 * 
	 * @return list hlášek
	 */
	public List<QuoteTO> getAllQuotes();

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 * 
	 * @return náhodná hláška
	 */
	public String getRandomQuote();

	/**
	 * Smaže hlášku
	 * 
	 * @param quoteId
	 *            id hlášky ke smazání
	 */
	public void deleteQuote(long quoteId);

}

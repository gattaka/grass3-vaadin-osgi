package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.model.dto.QuoteDTO;

public interface IQuotesFacade {

	/**
	 * Vytvoří a uloží novou hlášku dle textu
	 * 
	 * @param text
	 *            text hlášky
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean createNewQuote(String text);

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteDTO}
	 * 
	 * @return
	 */
	public List<QuoteDTO> getAllQuotes();

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 */
	public String getRandomQuote();

}

package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.interfaces.QuoteTO;

public interface QuotesFacade {

	/**
	 * Uloží hlášku
	 */
	public void saveQuote(QuoteTO quoteDTO);

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteTO}
	 * 
	 * @return
	 */
	public List<QuoteTO> getAllQuotes();

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 */
	public String getRandomQuote();

	/**
	 * Smaže hlášku
	 */
	public void deleteQuote(Long id);

}

package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.model.dto.QuoteDTO;

public interface QuotesFacade {

	/**
	 * Uloží hlášku
	 */
	public void saveQuote(QuoteDTO quoteDTO);

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

	/**
	 * Smaže hlášku
	 */
	public void deleteQuote(Long id);

}

package org.myftp.gattserver.grass3.facades;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.myftp.gattserver.grass3.model.dao.QuoteDAO;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum QuotesFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	/**
	 * Vytvoří a uloží novou hlášku dle textu
	 * 
	 * @param text
	 *            text hlášky
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean createNewQuote(String text) {
		Quote quote = new Quote();
		quote.setName(text);
		if (new QuoteDAO().save(quote) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteDTO}
	 * 
	 * @return
	 */
	public List<QuoteDTO> getAllQuotes() {
		List<Quote> quotes = new QuoteDAO().findAll();
		List<QuoteDTO> quoteDTOs = new ArrayList<QuoteDTO>();
		for (Quote quote : quotes) {
			quoteDTOs.add(mapper.map(quote));
		}
		return quoteDTOs;
	}

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 */
	public String getRandomQuote() {
		QuoteDAO quoteDAO = new QuoteDAO();
		Long count = quoteDAO.count();
		if (count == null) {
			return null;
		}
		Quote quote = null;
		if (count != 0) {
			Random generator = new Random();
			Long randomId = Math.abs(generator.nextLong()) % count + 1;
			quote = quoteDAO.findByID(randomId);
		}
		return quote == null ? "~ nebyly nalezeny žádné záznamy ~" : quote
				.getName();
	}

}

package org.myftp.gattserver.grass3.facades;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.model.dao.QuoteDAO;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public class QuotesFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

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
		// save sám uzavírá session
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
		QuoteDAO dao = new QuoteDAO();
		List<Quote> quotes = dao.findAll();
		List<QuoteDTO> quoteDTOs = new ArrayList<QuoteDTO>();
		for (Quote quote : quotes) {
			quoteDTOs.add(mapper.map(quote));
		}
		dao.closeSession();
		return quoteDTOs;
	}

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 */
	public String getRandomQuote() {
		QuoteDAO dao = new QuoteDAO();
		Long count = dao.count();
		if (count == null) {
			return null;
		}
		Quote quote = null;
		if (count != 0) {
			Random generator = new Random();
			Long randomId = Math.abs(generator.nextLong()) % count + 1;
			quote = dao.findByID(randomId);
			String quoteText = quote.getName();
			dao.closeSession();
			return quoteText;
		} else {
			return "~ nebyly nalezeny žádné záznamy ~";
		}
	}

}

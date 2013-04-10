package org.myftp.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IQuotesFacade;
import org.myftp.gattserver.grass3.model.dao.QuoteDAO;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.stereotype.Component;

@Component("quotesFacade")
public class QuotesFacadeImpl implements IQuotesFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Resource(name = "quoteDAO")
	private QuoteDAO quoteDAO;

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
		if (quoteDAO.save(quote) == null) {
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
		List<Quote> quotes = quoteDAO.findAll();
		List<QuoteDTO> quoteDTOs = new ArrayList<QuoteDTO>();
		for (Quote quote : quotes) {
			quoteDTOs.add(mapper.map(quote));
		}
		quoteDAO.closeSession();
		return quoteDTOs;
	}

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 */
	public String getRandomQuote() {
		Long count = quoteDAO.count();
		if (count == null) {
			return null;
		}
		Quote quote = null;
		if (count != 0) {
			Random generator = new Random();
			Long randomId = Math.abs(generator.nextLong()) % count + 1;
			quote = quoteDAO.findByID(randomId);
			String quoteText = quote.getName();
			quoteDAO.closeSession();
			return quoteText;
		} else {
			return "~ nebyly nalezeny žádné záznamy ~";
		}
	}

}

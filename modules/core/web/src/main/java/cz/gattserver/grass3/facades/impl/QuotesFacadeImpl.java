package cz.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.IQuotesFacade;
import cz.gattserver.grass3.model.dao.QuoteRepository;
import cz.gattserver.grass3.model.domain.Quote;
import cz.gattserver.grass3.model.dto.QuoteDTO;
import cz.gattserver.grass3.model.util.Mapper;

@Transactional
@Component("quotesFacade")
public class QuotesFacadeImpl implements IQuotesFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Autowired
	private QuoteRepository quoteRepository;

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
		return quoteRepository.save(quote) != null;
	}

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteDTO}
	 * 
	 * @return
	 */
	public List<QuoteDTO> getAllQuotes() {
		List<Quote> quotes = quoteRepository.findAll();
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
		long count = quoteRepository.count();
		if (count == 0)
			return "~ nebyly nalezeny žádné záznamy ~";

		Random generator = new Random();
		Long randomId = Math.abs(generator.nextLong()) % count + 1;

		Quote quote = quoteRepository.findOne(randomId);
		return quote.getName();
	}

}

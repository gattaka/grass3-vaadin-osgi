package cz.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.grass3.model.dao.QuoteRepository;
import cz.gattserver.grass3.model.domain.Quote;
import cz.gattserver.grass3.model.util.CoreMapper;

@Transactional
@Component
public class QuotesFacadeImpl implements QuotesFacade {

	@Autowired
	private CoreMapper mapper;

	@Autowired
	private QuoteRepository quoteRepository;

	/**
	 * Uloží hlášku
	 */
	public void saveQuote(QuoteTO quoteDTO) {
		Quote quote = new Quote();
		quote.setId(quoteDTO.getId());
		quote.setName(quoteDTO.getName());
		quoteRepository.save(quote);
	}

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteTO}
	 * 
	 * @return
	 */
	public List<QuoteTO> getAllQuotes() {
		List<Quote> quotes = quoteRepository.findAll();
		List<QuoteTO> quoteDTOs = new ArrayList<QuoteTO>();
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

	/**
	 * Smaže hlášku
	 */
	@Override
	public void deleteQuote(Long id) {
		quoteRepository.delete(id);
	}

}

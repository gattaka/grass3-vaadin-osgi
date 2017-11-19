package cz.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.facades.RandomSource;
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
	
	@Autowired
	private RandomSource randomSource;

	@Override
	public Long createQuote(String content) {
		Validate.notBlank(content, "Obsah hlášky nesmí být prázdný");
		Quote quote = new Quote();
		quote.setName(content);
		quote = quoteRepository.save(quote);
		return quote.getId();
	}

	@Override
	public void modifyQuote(Long quoteId, String modifiedContent) {
		Validate.notNull(quoteId, "quoteId hlášky nesmí být null");
		Validate.notBlank(modifiedContent, "Obsah hlášky nesmí být prázdný");
		Quote quote = new Quote();
		quote.setId(quoteId);
		quote.setName(modifiedContent);
		quoteRepository.save(quote);
	}

	@Override
	public List<QuoteTO> getAllQuotes() {
		List<Quote> quotes = quoteRepository.findAll();
		List<QuoteTO> quoteDTOs = new ArrayList<QuoteTO>();
		for (Quote quote : quotes) {
			quoteDTOs.add(mapper.map(quote));
		}
		return quoteDTOs;
	}

	@Override
	public String getRandomQuote() {
		long count = quoteRepository.count();
		if (count == 0)
			return "";
		return quoteRepository.findRandom(randomSource.getRandomNumber(count));
	}

	@Override
	public void deleteQuote(Long quoteId) {
		Validate.notNull(quoteId, "quoteId hlášky nesmí být null");
		quoteRepository.delete(quoteId);
	}

}

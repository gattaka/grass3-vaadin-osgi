package org.myftp.gattserver.grass3.facades;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.model.dto.QuoteDTO;

public class QuotesFacade {

	// Singleton stuff
	private static QuotesFacade instance;

	public static QuotesFacade getInstance() {
		if (instance == null)
			instance = new QuotesFacade();
		return instance;
	}

	private List<QuoteDTO> quotes;

	private QuotesFacade() {

		// TODO
		quotes = new ArrayList<QuoteDTO>();

		Long counter = 1L;

		QuoteDTO quoteDTO = new QuoteDTO();
		quoteDTO.setId(counter++);
		quoteDTO.setName("asdfsfsafsadfa asd sdf asdfs af sdf");
		quotes.add(quoteDTO);

		quoteDTO = new QuoteDTO();
		quoteDTO.setId(counter++);
		quoteDTO.setName("xxxxxxxxxxx x x xxxxxxxxxxxxxxxxxx   x");
		quotes.add(quoteDTO);

		quoteDTO = new QuoteDTO();
		quoteDTO.setId(counter++);
		quoteDTO.setName("aw ef  aweawefa weawefaw eaf  fewfaweaw");
		quotes.add(quoteDTO);

	}

	public int getQuotesCount() {
		// TODO
		return quotes.size();
	}

	public QuoteDTO getByID(Long id) {
		// TODO
		return quotes.get(id.intValue() - 1);
	}

	public List<QuoteDTO> findAllQuotes() {
		// TODO
		return quotes;
	}

}

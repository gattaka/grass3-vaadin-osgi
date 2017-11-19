package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.grass3.mock.MockRandomSourceImpl;
import cz.gattserver.grass3.test.GrassFacadeTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class QuotesFacadeTest extends GrassFacadeTest {

	@Autowired
	private QuotesFacade quotesFacade;

	@Test
	public void testGetAllQuotes() {
		Long quoteId = quotesFacade.createQuote("test");
		Long quoteId2 = quotesFacade.createQuote("test2");
		List<QuoteTO> quotes = quotesFacade.getAllQuotes();
		assertEquals(2, quotes.size());
		assertEquals(quoteId, quotes.get(0).getId());
		assertEquals("test", quotes.get(0).getName());
		assertEquals(quoteId2, quotes.get(1).getId());
		assertEquals("test2", quotes.get(1).getName());
	}

	@Test
	public void testGetRandomQuote() {
		assertEquals("", quotesFacade.getRandomQuote());
		quotesFacade.createQuote("test1");
		assertEquals("test1", quotesFacade.getRandomQuote());
		
		quotesFacade.createQuote("test2");
		quotesFacade.createQuote("test3");
		quotesFacade.createQuote("test4");
		
		MockRandomSourceImpl.nextValue = 2L;
		assertEquals("test2", quotesFacade.getRandomQuote());
		MockRandomSourceImpl.nextValue = 3L;
		assertEquals("test3", quotesFacade.getRandomQuote());
		MockRandomSourceImpl.nextValue = 4L;
		assertEquals("test4", quotesFacade.getRandomQuote());
	}

	@Test
	public void testDeleteQuote() {
		Long quoteId = quotesFacade.createQuote("test");
		quotesFacade.createQuote("test2");
		List<QuoteTO> quotes = quotesFacade.getAllQuotes();
		assertEquals(2, quotes.size());
		quotesFacade.deleteQuote(quoteId);
		quotes = quotesFacade.getAllQuotes();
		assertEquals(1, quotes.size());
	}

	@Test
	public void testCreateQuote() {
		Long quoteId = quotesFacade.createQuote("test");
		List<QuoteTO> quotes = quotesFacade.getAllQuotes();
		assertEquals(1, quotes.size());
		assertEquals(quoteId, quotes.get(0).getId());
		assertEquals("test", quotes.get(0).getName());
	}

	@Test
	public void testModifyQuote() {
		Long quoteId = quotesFacade.createQuote("test");
		List<QuoteTO> quotes = quotesFacade.getAllQuotes();
		assertEquals(1, quotes.size());
		assertEquals("test", quotes.get(0).getName());
		quotesFacade.modifyQuote(quoteId, "ehhh");
		quotes = quotesFacade.getAllQuotes();
		assertEquals(1, quotes.size());
		assertEquals("ehhh", quotes.get(0).getName());
	}

}

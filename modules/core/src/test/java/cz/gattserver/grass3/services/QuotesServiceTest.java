package cz.gattserver.grass3.services;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.QuoteTO;
import cz.gattserver.grass3.mock.MockRandomSourceImpl;
import cz.gattserver.grass3.services.QuotesService;
import cz.gattserver.grass3.test.GrassFacadeTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class QuotesServiceTest extends GrassFacadeTest {

	@Autowired
	private QuotesService quotesService;

	@Test
	public void testGetAllQuotes() {
		Long quoteId = quotesService.createQuote("test");
		Long quoteId2 = quotesService.createQuote("test2");
		List<QuoteTO> quotes = quotesService.getAllQuotes();
		assertEquals(2, quotes.size());
		assertEquals(quoteId, quotes.get(0).getId());
		assertEquals("test", quotes.get(0).getName());
		assertEquals(quoteId2, quotes.get(1).getId());
		assertEquals("test2", quotes.get(1).getName());
	}

	@Test
	public void testGetRandomQuote() {
		assertEquals("", quotesService.getRandomQuote());
		quotesService.createQuote("test1");
		assertEquals("test1", quotesService.getRandomQuote());

		quotesService.createQuote("test2");
		quotesService.createQuote("test3");
		quotesService.createQuote("test4");

		MockRandomSourceImpl.nextValue = 1L;
		assertEquals("test2", quotesService.getRandomQuote());
		MockRandomSourceImpl.nextValue = 2L;
		assertEquals("test3", quotesService.getRandomQuote());
		MockRandomSourceImpl.nextValue = 3L;
		assertEquals("test4", quotesService.getRandomQuote());
	}

	@Test
	public void testDeleteQuote() {
		Long quoteId = quotesService.createQuote("test");
		quotesService.createQuote("test2");
		List<QuoteTO> quotes = quotesService.getAllQuotes();
		assertEquals(2, quotes.size());
		quotesService.deleteQuote(quoteId);
		quotes = quotesService.getAllQuotes();
		assertEquals(1, quotes.size());
	}

	@Test
	public void testCreateQuote() {
		Long quoteId = quotesService.createQuote("test");
		List<QuoteTO> quotes = quotesService.getAllQuotes();
		assertEquals(1, quotes.size());
		assertEquals(quoteId, quotes.get(0).getId());
		assertEquals("test", quotes.get(0).getName());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateQuote_fail() {
		quotesService.createQuote(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateQuote_fail2() {
		quotesService.createQuote("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateQuote_fail3() {
		quotesService.createQuote(" ");
	}

	@Test
	public void testModifyQuote() {
		Long quoteId = quotesService.createQuote("test");
		List<QuoteTO> quotes = quotesService.getAllQuotes();
		assertEquals(1, quotes.size());
		assertEquals("test", quotes.get(0).getName());
		quotesService.modifyQuote(quoteId, "ehhh");
		quotes = quotesService.getAllQuotes();
		assertEquals(1, quotes.size());
		assertEquals("ehhh", quotes.get(0).getName());
	}

	@Test(expected = NullPointerException.class)
	public void testModifyQuote_fail2() {
		quotesService.modifyQuote(999L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyQuote_fail3() {
		quotesService.modifyQuote(999L, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyQuote_fail4() {
		quotesService.modifyQuote(999L, " ");
	}

}

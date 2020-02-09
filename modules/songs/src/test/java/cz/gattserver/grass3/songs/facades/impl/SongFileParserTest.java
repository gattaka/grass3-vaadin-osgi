package cz.gattserver.grass3.songs.facades.impl;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cz.gattserver.grass3.songs.model.interfaces.SongTO;

public class SongFileParserTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testOk() {
		SongTO to = SongFileParser.parseSongInfo("Autor - Název (2004).txt");
		assertEquals("Autor", to.getAuthor());
		assertEquals("Název", to.getName());
		assertEquals(2004, to.getYear().intValue());
	}

	@Test
	public void testOkYearInName() {
		SongTO to = SongFileParser.parseSongInfo("Autor - Název 2004.txt");
		assertEquals("Autor", to.getAuthor());
		assertEquals("Název 2004", to.getName());
		assertNull(to.getYear());
	}

	@Test
	public void testOkNoYear() {
		SongTO to = SongFileParser.parseSongInfo("Autor - Název.txt");
		assertEquals("Autor", to.getAuthor());
		assertEquals("Název", to.getName());
		assertNull(to.getYear());
	}

	@Test
	public void testOkSpaces() {
		SongTO to = SongFileParser.parseSongInfo("  Autor   -   Název   (2004)  .txt");
		assertEquals("Autor", to.getAuthor());
		assertEquals("Název", to.getName());
		assertEquals(2004, to.getYear().intValue());
	}

	@Test
	public void testOkSpacesNoYear() {
		SongTO to = SongFileParser.parseSongInfo("  Autor   -   Název   .txt");
		assertEquals("Autor", to.getAuthor());
		assertEquals("Název", to.getName());
		assertNull(to.getYear());
	}

	@Test
	public void testAuthorFail() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage(SongFileParser.AUTHOR_ERR);
		SongFileParser.parseSongInfo("Název (2004).txt");
	}

	@Test
	public void testNameFail() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage(SongFileParser.NAME_ERR);
		SongFileParser.parseSongInfo("Autor - (2004).txt");
	}

	@Test
	public void testNameFail2() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage(SongFileParser.NAME_ERR);
		SongFileParser.parseSongInfo("Autor - .txt");
	}

	@Test
	public void testYearFail() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage(SongFileParser.YEAR_ERR);
		SongFileParser.parseSongInfo("Autor - Název (090s).txt");
	}

}

package cz.gattserver.grass3.articles.plugins.favlink;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;

public class FaviconUtilsTest {

	@Test
	public void testCreateCachedFaviconAddress() {
		assertEquals("/test-root/articles-favlink-plugin/favicon-name.png",
				FaviconUtils.createCachedFaviconAddress("/test-root", "favicon-name.png"));
	}
	
	@Test
	public void testCreateCachedFaviconAddress2() {
		assertEquals("/articles-favlink-plugin/favicon-name.png",
				FaviconUtils.createCachedFaviconAddress("", "favicon-name.png"));
	}

	@Test(expected = NullPointerException.class)
	public void testCreateCachedFaviconAddress_fail() {
		assertEquals("/articles-favlink-plugin/favicon-name.png",
				FaviconUtils.createCachedFaviconAddress(null, "favicon-name.png"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCachedFaviconAddress_fail2() {
		assertEquals("/articles-favlink-plugin/favicon-name.png", FaviconUtils.createCachedFaviconAddress("dd", ""));
	}

	@Test
	public void testCreateFaviconRootFilename() throws MalformedURLException {
		assertEquals("www.testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("https://www.testweb.cz/")));
		assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("https://testweb.cz/")));
		assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("http://testweb.cz/")));
		assertEquals("testweb.cz", FaviconUtils.createFaviconRootFilename(new URL("http://testweb.cz")));
		assertEquals("www.testweb2.org", FaviconUtils.createFaviconRootFilename(new URL(
				"https://www.testweb2.org/policie-evakuovala-mestsky-soud-ve-slezske-kvuli-nahlasene-bombe-p9f-/zpravy-domov.aspx?c=A171207_114412_ln_domov_ele#utm_source=rss&utm_medium=feed&utm_campaign=ln_testweb&utm_content=main")));
	}

	@Test
	public void testGetPageURL() throws MalformedURLException {
		assertEquals(new URL("https://www.testweb.cz/"), FaviconUtils.getPageURL(("https://www.testweb.cz/")));
		assertEquals(new URL("http://www.testweb.cz/"), FaviconUtils.getPageURL(("http://www.testweb.cz/")));
	}

	@Test(expected = ParserException.class)
	public void testGetPageURL_fail() throws MalformedURLException {
		FaviconUtils.getPageURL("www.testweb.cz");
	}

}

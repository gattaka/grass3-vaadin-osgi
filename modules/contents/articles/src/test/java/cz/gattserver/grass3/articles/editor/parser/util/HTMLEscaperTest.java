package cz.gattserver.grass3.articles.editor.parser.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class HTMLEscaperTest {

	@Test
	public void test() {
		String safeText = HTMLEscaper.stringToHTMLString("test <strong>text</strong>");
		assertEquals("test &lt;strong&gt;text&lt;/strong&gt;", safeText);
	}
	
	@Test
	public void testHref() {
		String safeText = HTMLEscaper.stringToHTMLString("odkaz: <a href=\"adddr&param\">link</a>");
		assertEquals("odkaz: &lt;a href=&quot;adddr&amp;param&quot;&gt;link&lt;/a&gt;", safeText);
	}
	
	@Test
	public void testEmpty() {
		String safeText = HTMLEscaper.stringToHTMLString("");
		assertEquals("", safeText);
	}
	

}

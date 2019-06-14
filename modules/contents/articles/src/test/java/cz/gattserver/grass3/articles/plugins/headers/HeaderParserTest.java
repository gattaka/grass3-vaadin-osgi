package cz.gattserver.grass3.articles.plugins.headers;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class HeaderParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test() {
		HeaderParser parser = new HeaderParser(2, "H2");
		Element element = parser.parse(getParsingProcessorWithText("[H2]Hypertext Markup Language[/H2]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<div class=\"articles-h2\">Hypertext Markup Language <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level2\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testAfterLine() {
		HeaderParser parser = new HeaderParser(2, "H2");
		ParsingProcessor parsingProcessor = getParsingProcessorWithText("[H2]Hypertext Markup Language[/H2]\nfdd");
		Element element = parser.parse(parsingProcessor);

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<div class=\"articles-h2\">Hypertext Markup Language <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level2\"></div>",
				ctx.getOutput());
		assertEquals("fdd", parsingProcessor.getText());
	}

	@Test
	public void testAfterLine2() {
		HeaderParser parser = new HeaderParser(2, "H2");
		ParsingProcessor parsingProcessor = getParsingProcessorWithText("[H2]Hypertext Markup Language[/H2]\n\nfdd");
		Element element = parser.parse(parsingProcessor);

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<div class=\"articles-h2\">Hypertext Markup Language <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level2\"></div>",
				ctx.getOutput());
		assertEquals("fdd", parsingProcessor.getText());
	}

	@Test(expected = TokenException.class)
	public void testFailMissingEndTag() {
		HeaderParser parser = new HeaderParser(2, "H2");
		Element element = parser.parse(getParsingProcessorWithText("[H2]HTML[H2]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void testFailEOF() {
		HeaderParser parser = new HeaderParser(2, "H2");
		Element element = parser.parse(getParsingProcessorWithText("[H2]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void testFailStartTag() {
		HeaderParser parser = new HeaderParser(2, "H2");
		Element element = parser.parse(getParsingProcessorWithText("[BAD_TAG]Hypertext Markup Language[/H2]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void testFailEndTag() {
		HeaderParser parser = new HeaderParser(2, "H2");
		Element element = parser.parse(getParsingProcessorWithText("[H2]Hypertext Markup Language[/BAD_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void testFailEndTag2() {
		HeaderParser parser = new HeaderParser(2, "H2");
		Element element = parser.parse(getParsingProcessorWithText("[H2]Hypertext Markup Language[/BAD_TAG] fddf"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}

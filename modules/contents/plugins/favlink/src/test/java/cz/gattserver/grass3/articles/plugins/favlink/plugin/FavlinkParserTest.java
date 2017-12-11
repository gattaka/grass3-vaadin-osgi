package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.favlink.test.MockFaviconObtainStrategy;

public class FavlinkParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	private FavlinkParser createMockParser() {
		return new FavlinkParser("MOCK", Arrays.asList(new MockFaviconObtainStrategy()));
	}

	@Test
	public void test() {
		Element element = createMockParser().parse(getParsingProcessorWithText("[MOCK]http://test.mock.neco[/MOCK]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<a href=\"http://test.mock.neco\" ><img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"http://mock.neco/favicon.png\" />http://test.mock.neco</a>",
				ctx.getOutput());
	}

	@Test(expected = TokenException.class)
	public void test_failBadStartTag() {
		Element element = createMockParser().parse(getParsingProcessorWithText("[BAD_TAG]HTML[/MOCK]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadEndTag() {
		Element element = createMockParser().parse(getParsingProcessorWithText("[MOCK]HTML[/BAD_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}

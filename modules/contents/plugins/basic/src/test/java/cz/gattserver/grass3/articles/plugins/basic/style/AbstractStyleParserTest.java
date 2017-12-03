package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class AbstractStyleParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	private AbstractStyleParser createMockParser() {
		return new AbstractStyleParser("MOCK") {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new AbstractStyleElement(elist) {

					@Override
					protected void generateStartTag(Context ctx) {
						ctx.print("<mock>");
					}

					@Override
					protected void generateEndTag(Context ctx) {
						ctx.print("</mock>");
					}
				};
			}
		};
	}

	@Test
	public void test() {
		Element element = createMockParser().parse(getParsingProcessorWithText("[MOCK]HTMLsample[/MOCK]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<mock>HTMLsample</mock>", ctx.getOutput());
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

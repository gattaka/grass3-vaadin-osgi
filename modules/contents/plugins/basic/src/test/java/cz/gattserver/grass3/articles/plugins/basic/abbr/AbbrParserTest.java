package cz.gattserver.grass3.articles.plugins.basic.abbr;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.basic.abbr.AbbrParser;

public class AbbrParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test() {
		AbbrParser abbrElement = new AbbrParser("ABBR", "T");
		Element element = abbrElement
				.parse(getParsingProcessorWithText("[ABBR]HTML[T]Hypertext Markup Language[/T][/ABBR]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertTrue(ctx.getOutput().endsWith("<abbr title=\"Hypertext Markup Language\">HTML</abbr>"));
	}

}

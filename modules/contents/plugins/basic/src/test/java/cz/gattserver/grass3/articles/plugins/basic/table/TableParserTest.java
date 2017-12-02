package cz.gattserver.grass3.articles.plugins.basic.table;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class TableParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test_empty() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("", ctx.getOutput());
	}

	@Test
	public void test_emptyCell() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]\t[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td></td></tr>" + "</table>",
				ctx.getOutput());
	}

	@Test
	public void test_emptyLine() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]\n[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("", ctx.getOutput());
	}

	@Test
	public void test() {
		TableParser parser = new TableParser("CUSTOM_TAG", false);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]ab c\tde\nfg[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>ab c</td><td>de</td></tr>" + "<tr><td>fg</td><td></td></tr>" + "</table>",
				ctx.getOutput());
	}

	// @Test(expected = TokenException.class)
	// public void test_failAbbrEOF() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element =
	// abbrElement.parse(getParsingProcessorWithText("[CUSTOM_TAG]HTML[CUSTOM_TAG2]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failTitleEOF() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element =
	// abbrElement.parse(getParsingProcessorWithText("[CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failBadAbbrStartTag() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element = abbrElement.parse(getParsingProcessorWithText(
	// "[BAD_TAG]HTML[CUSTOM_TAG2]Hypertext Markup
	// Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failBadAbbrEndTag() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element = abbrElement.parse(getParsingProcessorWithText(
	// "[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup
	// Language[/CUSTOM_TAG2][/BAD_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failBadTitleStartTag() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element = abbrElement.parse(getParsingProcessorWithText(
	// "[CUSTOM_TAG]HTML[BAD_TAG2]Hypertext Markup
	// Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failBadTitleEndTag() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element = abbrElement.parse(getParsingProcessorWithText(
	// "[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup
	// Language[/BAD_TAG2][/CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failMandatoryAbbr() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element = abbrElement
	// .parse(getParsingProcessorWithText("[CUSTOM_TAG][CUSTOM_TAG2][/CUSTOM_TAG2][/CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failMandatoryTitle() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element = abbrElement
	// .parse(getParsingProcessorWithText("[CUSTOM_TAG]
	// [CUSTOM_TAG2][/CUSTOM_TAG2][/CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }
	//
	// @Test(expected = TokenException.class)
	// public void test_failMandatorySubtag() {
	// AbbrParser abbrElement = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
	// Element element =
	// abbrElement.parse(getParsingProcessorWithText("[CUSTOM_TAG][/CUSTOM_TAG]"));
	// Context ctx = new ContextImpl();
	// element.apply(ctx);
	// }

}

package cz.gattserver.grass3.articles.plugins.basic.list;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.Plugin;

public class ListParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		// ať zná aspoň sebe
		OrderedListPlugin plugin = new OrderedListPlugin();
		Map<String, Plugin> map = new HashMap<>();
		map.put(plugin.getTag(), plugin);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", map);
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]test[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>", ctx.getOutput());
	}

	@Test
	public void testMultiline() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]test\nhehe\naa[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li><li>hehe</li><li>aa</li></ol>", ctx.getOutput());
	}
	
	@Test
	public void testEmptyAfterline() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]test\n[/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>", ctx.getOutput());
	}

	@Test
	public void testSub() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][OL]test\nhehe[/OL][/CUSTOM_TAG]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li><ol><li>test</li><li>hehe</li></ol></li></ol>", ctx.getOutput());
	}

	@Test
	public void testBreaklineAfterBlock() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]test[/CUSTOM_TAG]\n"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<ol><li>test</li></ol>", ctx.getOutput());
	}

	@Test(expected = TokenException.class)
	public void test_failBadStartTag() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[BAD_TAG]test[/CUSTOM_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failBadEndTag() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]test[/BAD_TAG]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

	@Test(expected = TokenException.class)
	public void test_failMissingEnd() {
		ListParser parser = new ListParser("CUSTOM_TAG", true);
		Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]test"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
	}

}

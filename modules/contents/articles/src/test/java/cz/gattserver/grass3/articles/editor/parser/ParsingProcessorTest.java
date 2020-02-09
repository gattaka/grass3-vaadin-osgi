package cz.gattserver.grass3.articles.editor.parser;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.impl.ArticleParser;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class ParsingProcessorTest extends AbstractContextAwareTest {

	@Autowired
	private PluginRegisterService pluginRegister;

	@Test
	public void testParsingError() {
		Lexer lexer = new Lexer("[MOCK_TAG]z");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals(
				"<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; "
						+ "font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">"
						+ "Plugin 'MOCK_TAG' encountered parsing error: java.lang.NumberFormatException: For input string: \"z\" (path: [])</span>z",
				ctx.getOutput());
	}

	@Test
	public void testParsingError2() {
		Lexer lexer = new Lexer("[MOCK_TAG]z[/MOCK_TAG]");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals(
				"<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; "
						+ "font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">"
						+ "Plugin 'MOCK_TAG' encountered parsing error: java.lang.NumberFormatException: For input string: \"z\" (path: [])</span>z[/MOCK_TAG]",
				ctx.getOutput());
	}

	@Test
	public void testParsingError3() {
		Lexer lexer = new Lexer("[MOCK_TAG]7[/ff");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals(
				"<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; "
						+ "font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">"
						+ "Plugin 'MOCK_TAG' encountered parsing error: Expected Token: END_TAG Actual Token: TEXT ([/ff) (path: [])</span>[/ff",
				ctx.getOutput());
	}

	@Test
	public void testParsingError4() {
		Lexer lexer = new Lexer("[MOCK_TAG]0[/MOCK_TAG]");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals("<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; "
				+ "font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">"
				+ "Plugin 'MOCK_TAG' throws exception</span>[/MOCK_TAG]", ctx.getOutput());
	}

	@Test
	public void testParsingErrorActiveBlockMismatch() {
		Lexer lexer = new Lexer("[N1][N2]test[/N1]");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals(
				"<div class=\"articles-h1\"><span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; font-family: monospace; "
						+ "font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">Plugin 'N2' encountered parsing error: "
						+ "Expected content: N2 Actual content: N1 (path: [N1])</span> <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testParsingErrorActiveBlockEOF() {
		Lexer lexer = new Lexer("[N1][N2]test");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals(
				"<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; "
						+ "font-variant: normal; background: #ffcc33;\">Plugin 'N1' encountered parsing error: Expected Token: END_TAG (N1) Actual Token: EOF (path: [])</span>",
				ctx.getOutput());
	}

	@Test
	public void testParsingIgnoreEolInBlock() {
		Lexer lexer = new Lexer("[N1]test\nTEST[/N1]");
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot",
				pluginRegister.createRegisterSnapshot());
		ArticleParser articleParser = new ArticleParser();
		Element tree = articleParser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		assertEquals(
				"<div class=\"articles-h1\">test<br/>TEST <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level1\"></div>",
				ctx.getOutput());
	}
}

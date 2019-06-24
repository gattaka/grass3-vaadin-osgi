package cz.gattserver.grass3.articles.plugins.code;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class CodeParserTest {

	private ParsingProcessor getParsingProcessorWithText(String text) {
		Lexer lexer = new Lexer(text);
		ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	private CodeParser createMockParser() {
		return new CodeParser("MOCK", "desc", "dummyLib", "dummyMode");
	}

	@Test
	public void test() {
		Element element = createMockParser().parse(
				getParsingProcessorWithText("[MOCK]Context ctx = new ContextImpl();\nelement.apply(ctx);[/MOCK]"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">desc</span><div class=\"barier\">"
				/*		*/ + "<div class=\"numberedtext\"><textarea name=\"codemirror_dummyMode\">"
				/*			*/ + "Context ctx = new ContextImpl();\nelement.apply(ctx);</textarea>"
				/*		*/ + "</div>"
				/*	*/ + "</div>" + "<div id=\"code_koncovka\">" + "</div>", ctx.getOutput());
	}

	@Test
	public void testIgnoreAfterBreakline() {
		Element element = createMockParser().parse(
				getParsingProcessorWithText("[MOCK]Context ctx = new ContextImpl();\nelement.apply(ctx);[/MOCK]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">desc</span><div class=\"barier\">"
				/*		*/ + "<div class=\"numberedtext\"><textarea name=\"codemirror_dummyMode\">"
				/*			*/ + "Context ctx = new ContextImpl();\nelement.apply(ctx);</textarea>"
				/*		*/ + "</div>"
				/*	*/ + "</div>" + "<div id=\"code_koncovka\">" + "</div>", ctx.getOutput());
	}

	@Test
	public void testEmptyLines() {
		Element element = createMockParser().parse(getParsingProcessorWithText(
				"[MOCK]\nContext ctx = new ContextImpl();\n\nelement.apply(ctx);\n[/MOCK]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">desc</span><div class=\"barier\">"
				/*		*/ + "<div class=\"numberedtext\"><textarea name=\"codemirror_dummyMode\">"
				/*			*/ + "\nContext ctx = new ContextImpl();\n \nelement.apply(ctx);\n</textarea>"
				/*		*/ + "</div>"
				/*	*/ + "</div>" + "<div id=\"code_koncovka\">" + "</div>", ctx.getOutput());
	}

	@Test
	public void testBASH() {
		Element element = new BASHCodePlugin().getParser().parse(getParsingProcessorWithText("[BASH]code[/BASH]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<span class=\"lang_description\">BASH</span><div class=\"barier\"><div class=\"numberedtext\"><textarea name=\"codemirror_shell\">code</textarea>"
						+ "</div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testCCode() {
		Element element = new CCodePlugin().getParser().parse(getParsingProcessorWithText("[C]code[/C]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">C</span><div class=\"barier\"><div class=\"numberedtext\">"
				+ "<textarea name=\"codemirror_c\">code</textarea>" + "</div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testCode() {
		Element element = new CodePlugin().getParser().parse(getParsingProcessorWithText("[CODE]code[/CODE]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">Code</span><div class=\"barier\"><div class=\"numberedtext\">"
				+ "<textarea name=\"codemirror_\">code</textarea>" + "</div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testCPPCode() {
		Element element = new CPPCodePlugin().getParser().parse(getParsingProcessorWithText("[CPP]code[/CPP]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">C++</span><div class=\"barier\"><div class=\"numberedtext\">"
				+ "<textarea name=\"codemirror_cpp\">code</textarea>" + "</div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testCSharpCode() {
		Element element = new CSharpCodePlugin().getParser()
				.parse(getParsingProcessorWithText("[CSHARP]code[/CSHARP]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">C#</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_csharp\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testCSSCode() {
		Element element = new CSSCodePlugin().getParser().parse(getParsingProcessorWithText("[CSS]code[/CSS]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">CSS</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_css\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testGroovyCode() {
		Element element = new GroovyCodePlugin().getParser()
				.parse(getParsingProcessorWithText("[GROOVY]code[/GROOVY]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">Groovy</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_groovy\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testJavaCode() {
		Element element = new JavaCodePlugin().getParser().parse(getParsingProcessorWithText("[JAVA]code[/JAVA]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">Java</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_java\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testJavaScriptCode() {
		Element element = new JavaScriptCodePlugin().getParser().parse(getParsingProcessorWithText("[JS]code[/JS]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<span class=\"lang_description\">JavaScript</span><div class=\"barier\"><div class=\"numberedtext\">"
						+ "<textarea name=\"codemirror_js\">code</textarea>"
						+ "</div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testLatexCode() {
		Element element = new LatexCodePlugin().getParser().parse(getParsingProcessorWithText("[LATEX]code[/LATEX]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">LaTeX</span><div class=\"barier\"><div class=\"numberedtext\">"
				+ "<textarea name=\"codemirror_latex\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testPHPCode() {
		Element element = new PHPCodePlugin().getParser().parse(getParsingProcessorWithText("[PHP]code[/PHP]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<span class=\"lang_description\">PHP</span><div class=\"barier\"><div class=\"numberedtext\">"
						+ "<textarea name=\"codemirror_php\">code</textarea></div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testPythonCode() {
		Element element = new PythonCodePlugin().getParser()
				.parse(getParsingProcessorWithText("[PYTHON]code[/PYTHON]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">Python</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_python\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testRubyCode() {
		Element element = new RubyCodePlugin().getParser().parse(getParsingProcessorWithText("[RUBY]code[/RUBY]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<span class=\"lang_description\">Ruby</span><div class=\"barier\">"
						+ "<div class=\"numberedtext\"><textarea name=\"codemirror_ruby\">code</textarea></div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testScalaCode() {
		Element element = new ScalaCodePlugin().getParser().parse(getParsingProcessorWithText("[SCALA]code[/SCALA]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals(
				"<span class=\"lang_description\">Scala</span><div class=\"barier\"><div class=\"numberedtext\">"
						+ "<textarea name=\"codemirror_scala\">code</textarea></div></div><div id=\"code_koncovka\"></div>",
				ctx.getOutput());
	}

	@Test
	public void testSQLCode() {
		Element element = new SQLCodePlugin().getParser().parse(getParsingProcessorWithText("[SQL]code[/SQL]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">SQL</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_sql\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testTypeScriptCode() {
		Element element = new TypeScriptCodePlugin().getParser().parse(getParsingProcessorWithText("[TS]code[/TS]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">TypeScript</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_ts\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
	}

	@Test
	public void testXMLCode() {
		Element element = new XMLCodePlugin().getParser().parse(getParsingProcessorWithText("[XML]code[/XML]\n"));
		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertEquals("<span class=\"lang_description\">XML</span><div class=\"barier\">"
				+ "<div class=\"numberedtext\"><textarea name=\"codemirror_xml\">code</textarea>"
				+ "</div></div><div id=\"code_koncovka\"></div>", ctx.getOutput());
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

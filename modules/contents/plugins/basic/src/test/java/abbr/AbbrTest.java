package abbr;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.lexer.Lexer;
import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.PluginBag;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass3.articles.plugins.basic.abbr.AbbrParser;

public class AbbrTest {

	private PluginBag getBagWithText(String text) {
		Lexer lexer = new Lexer(text);
		PluginBag pluginBag = new PluginBag(lexer, "contextRoot");
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test() {
		AbbrParser abbrElement = new AbbrParser("ABBR", "T");
		Element element = abbrElement.parse(getBagWithText("[ABBR]HTML[T]Hypertext Markup Language[/T][/ABBR]"));

		Context ctx = new ContextImpl();
		element.apply(ctx);
		assertTrue(ctx.getOutput().endsWith("<abbr title=\"Hypertext Markup Language\">HTML</abbr>"));
	}

}

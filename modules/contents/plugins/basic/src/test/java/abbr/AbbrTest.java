package abbr;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.basic.abbr.AbbrElement;
import cz.gattserver.grass3.articles.editor.api.ContextImpl;
import cz.gattserver.grass3.articles.lexer.Lexer;
import cz.gattserver.grass3.articles.parser.PluginBag;
import cz.gattserver.grass3.articles.parser.PluginRegister;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public class AbbrTest {

	private PluginBag getBagWithText(String text) {
		Lexer lexer = new Lexer(text);
		PluginBag pluginBag = new PluginBag(lexer, "contextRoot", new PluginRegister());
		pluginBag.nextToken(); // musí se inicializovat
		return pluginBag;
	}

	@Test
	public void test() {

		AbbrElement abbrElement = new AbbrElement("ABBR","T");
		AbstractElementTree tree = abbrElement
				.parse(getBagWithText("[ABBR]HTML[T]Hypertext Markup Language[/T][/ABBR]"));

		IContext ctx = new ContextImpl();
		tree.generate(ctx);
		assertTrue(ctx.getOutput().endsWith(
				"<abbr title=\"Hypertext Markup Language\">HTML</abbr>"));
	}
	
}

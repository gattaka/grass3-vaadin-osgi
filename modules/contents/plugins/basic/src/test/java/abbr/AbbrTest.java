package abbr;

import static org.junit.Assert.*;

import org.junit.Test;
import org.myftp.gattserver.grass3.articles.editor.api.ContextImpl;
import org.myftp.gattserver.grass3.articles.lexer.Lexer;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;
import org.myftp.gattserver.grass3.articles.basic.abbr.AbbrElement;


public class AbbrTest {

	private PluginBag getBagWithText(String text) {
		Lexer lexer = new Lexer(text);
		PluginBag pluginBag = new PluginBag(lexer, "contextRoot");
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

package cz.gattserver.grass3.articles.plugins.headers;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class HeaderElementTest {

	@Test
	public void test() {
		HeaderElement e = new HeaderElement(Arrays.asList(new TextElement("Huhůůů")), 2);
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals(
				"<div class=\"articles-h2\">Huhůůů <a class=\"articles-h-id\" href=\"0\"></a></div><div class=\"level2\"></div>",
				out);
	}

}

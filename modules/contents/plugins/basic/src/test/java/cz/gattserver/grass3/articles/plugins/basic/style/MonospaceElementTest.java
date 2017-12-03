package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class MonospaceElementTest {

	@Test
	public void test() {
		MonospaceElement e = new MonospaceElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<span class=\"articles-basic-monospaced\">neco</span>", out);
		assertTrue(ctx.getCSSResources().contains("articles/basic/style.css"));
	}

}

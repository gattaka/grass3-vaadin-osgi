package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class SupElementTest {

	@Test
	public void test() {
		SupElement e = new SupElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<sup>neco</sup>", out);
	}

}

package cz.gattserver.grass3.articles.plugins.basic.style.color;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class RedElementTest {

	@Test
	public void test() {
		RedElement e = new RedElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<span style='color: red'>neco</span>", out);
	}

}

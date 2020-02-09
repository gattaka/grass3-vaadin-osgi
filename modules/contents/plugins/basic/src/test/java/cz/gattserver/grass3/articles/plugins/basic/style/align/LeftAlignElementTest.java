package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class LeftAlignElementTest {

	@Test
	public void test() {
		LeftAlignElement e = new LeftAlignElement(Arrays.asList(new TextElement("neco")));
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<div style='text-align: left'>neco</div>", out);
	}

}

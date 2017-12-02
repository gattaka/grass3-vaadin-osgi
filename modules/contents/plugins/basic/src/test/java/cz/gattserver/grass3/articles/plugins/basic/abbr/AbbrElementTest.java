package cz.gattserver.grass3.articles.plugins.basic.abbr;

import static org.junit.Assert.*;

import org.junit.Test;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class AbbrElementTest {

	@Test
	public void test() {
		AbbrElement e = new AbbrElement("testText", "testTitle");
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals("<abbr title=\"testTitle\">testText</abbr>", out);
	}

}

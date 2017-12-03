package cz.gattserver.grass3.articles.plugins.basic.image;

import static org.junit.Assert.*;

import org.junit.Test;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class ImageElementTest {

	@Test
	public void test() {
		ImageElement e = new ImageElement("http://link.to/image");
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals(
				"<a target=\"_blank\" href=\"http://link.to/image\"><img class=\"articles-basic-img\" src=\"http://link.to/image\" alt=\"http://link.to/image\" /></a>",
				out);
	}

}

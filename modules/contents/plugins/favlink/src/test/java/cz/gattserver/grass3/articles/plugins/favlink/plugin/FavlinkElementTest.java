package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class FavlinkElementTest {

	@Test
	public void test() {
		FavlinkElement element = new FavlinkElement("mock/imgs/favicon/fav.ico", "http://mock.url.website");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals(
				"<a href=\"http://mock.url.website\" >"
						+ "<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"mock/imgs/favicon/fav.ico\" />http://mock.url.website</a>",
				context.getOutput());
	}

	@Test
	public void test_empty() {
		FavlinkElement element = new FavlinkElement(null, "http://mock.url.website");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals("<a href=\"http://mock.url.website\" >http://mock.url.website</a>", context.getOutput());
	}

}

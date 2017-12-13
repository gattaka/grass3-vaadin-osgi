package cz.gattserver.grass3.articles.plugins.favlink.server;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class FavlinkImageRequestHandlerTest extends AbstractContextAwareTest {

	@Test
	public void testHeaderFaviconObtainStrategy_empty() throws IOException {
		FavlinkImageRequestHandler handler = new FavlinkImageRequestHandler();
		File file = handler.getFile("testfile");
		assertEquals(new File(new File("favlink", "cache"), "testfile"), file);
	}

}

package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass3.articles.plugins.favlink.test.MockFileSystemService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class CombinedFaviconObtainStrategyTest extends AbstractContextAwareTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	private ClientAndServer mockServer;

	@Before
	public void init() {
		fileSystemService.init();
		mockServer = startClientAndServer(1929);
	}

	@After
	public void stopProxy() {
		mockServer.stop();
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_empty() throws IOException {
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_empty.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
		assertNull(link);
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_cached() throws IOException {
		FaviconCache cache = new FaviconCache();
		Path cacheDir = cache.getCacheDirectoryPath();
		Files.createFile(cacheDir.resolve("localhost.ico"));

		FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_address() throws IOException {
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("addressFaviconMockHTML.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/favicon.png"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);
	}

	@Test
	public void testCombinedFaviconObtainStrategyTest_header() throws IOException {
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_http_png.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/dummy/site"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/mockFavicon.png"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/mockFavicon.png"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		FaviconObtainStrategy strategy = new CombinedFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929/dummy/site", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);
	}

}

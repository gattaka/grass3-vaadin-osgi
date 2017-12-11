package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

import cz.gattserver.grass3.articles.plugins.favlink.test.MockFileSystemService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class HeaderFaviconObtainStrategyTest extends AbstractContextAwareTest {

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
	public void testHeaderFaviconObtainStrategy_http() throws IOException {
		// server
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/headerFaviconMockHTML.png"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/headerFaviconMockHTML.png"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));

		HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

		assertTrue(Files.exists(mockDir));
		assertTrue(Files.exists(mockDir.resolve("localhost.png")));
	}
	
	@Test
	public void testHeaderFaviconObtainStrategy_slashed() throws IOException {
		// server
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/headerFaviconMockHTML.png"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/headerFaviconMockHTML.png"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));

		HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

		assertTrue(Files.exists(mockDir));
		assertTrue(Files.exists(mockDir.resolve("localhost.png")));
	}
	
	@Test
	public void testHeaderFaviconObtainStrategy_slashed2() throws IOException {
		// server
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed2.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/headerFaviconMockHTML.png"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/headerFaviconMockHTML.png"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));

		HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.png", link);

		assertTrue(Files.exists(mockDir));
		assertTrue(Files.exists(mockDir.resolve("localhost.png")));
	}
	
	@Test
	public void testHeaderFaviconObtainStrategy_http_ico() throws IOException {
		// server
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_ico.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/headerFaviconMockHTML.ico"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/headerFaviconMockHTML.ico"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));

		HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

		assertTrue(Files.exists(mockDir));
		assertTrue(Files.exists(mockDir.resolve("localhost.ico")));
	}
	
	@Test
	public void testHeaderFaviconObtainStrategy_slashed_ico() throws IOException {
		// server
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed_ico.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/headerFaviconMockHTML.ico"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/headerFaviconMockHTML.ico"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));

		HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

		assertTrue(Files.exists(mockDir));
		assertTrue(Files.exists(mockDir.resolve("localhost.ico")));
	}
	
	@Test
	public void testHeaderFaviconObtainStrategy_slashed2_ico() throws IOException {
		// server
		MockServerClient msc = new MockServerClient("localhost", 1929);

		String page = IOUtils.toString(this.getClass().getResourceAsStream("headerFaviconMockHTML_slashed2_ico.html"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/"))
				.respond(new HttpResponse().withStatusCode(200).withBody(page));

		byte[] favicon = IOUtils.toByteArray(this.getClass().getResourceAsStream("imgadr/headerFaviconMockHTML.ico"));
		msc.when(new HttpRequest().withMethod("GET").withPath("/imgadr/headerFaviconMockHTML.ico"))
				.respond(new HttpResponse().withStatusCode(200).withBody(favicon));

		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));

		HeaderFaviconObtainStrategy strategy = new HeaderFaviconObtainStrategy();
		String link = strategy.obtainFaviconURL("http://localhost:1929", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/localhost.ico", link);

		assertTrue(Files.exists(mockDir));
		assertTrue(Files.exists(mockDir.resolve("localhost.ico")));
	}

}

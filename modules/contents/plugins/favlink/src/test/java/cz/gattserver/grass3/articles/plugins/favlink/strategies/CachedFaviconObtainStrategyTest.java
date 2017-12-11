package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.plugins.favlink.test.MockFileSystemService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class CachedFaviconObtainStrategyTest extends AbstractContextAwareTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Before
	public void init() {
		fileSystemService.init();
	}

	@Test
	public void testCacheFaviconObtainStrategy_missed() throws IOException {
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy() {

			@Override
			protected String onCacheMiss(URL pageURL, Path cacheDir, String faviconRootFilename) {
				// nic
				return null;
			}
		};
		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		assertFalse(Files.exists(mockDir));
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertTrue(Files.exists(mockDir));
		assertNull(link);
	}

	@Test
	public void testCacheFaviconObtainStrategy_created() throws IOException {
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy() {

			@Override
			protected String onCacheMiss(URL pageURL, Path cacheDir, String faviconRootFilename) {
				try {
					String fileName = faviconRootFilename + ".png";
					Files.createFile(cacheDir.resolve(fileName));
					return fileName;
				} catch (IOException e) {
					throw new IllegalStateException("fail!");
				}
			}
		};
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/www.test.cz.png", link);
	}

	@Test
	public void testCacheFaviconObtainStrategy_cached() throws IOException {
		Path mockDir = fileSystemService.getFileSystem().getPath("favlink", "cache");
		Files.createDirectories(mockDir);
		Files.createFile(mockDir.resolve("www.test.cz.png"));
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy() {

			@Override
			protected String onCacheMiss(URL pageURL, Path cacheDir, String faviconRootFilename) {
				// nic
				return null;
			}
		};
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/www.test.cz.png", link);
	}

}

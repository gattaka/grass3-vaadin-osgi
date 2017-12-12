package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.plugins.favlink.FaviconCache;
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
		FaviconCache cache = new FaviconCache();
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy(cache);
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertNull(link);
	}

	@Test
	public void testCacheFaviconObtainStrategy_cached() throws IOException {
		FaviconCache cache = new FaviconCache();
		Path cacheDir = cache.getCacheDirectoryPath();
		Files.createFile(cacheDir.resolve("www.test.cz.png"));
		CacheFaviconObtainStrategy strategy = new CacheFaviconObtainStrategy(cache);
		String link = strategy.obtainFaviconURL("http://www.test.cz", "mycontextroot");
		assertEquals("mycontextroot/articles-favlink-plugin/www.test.cz.png", link);
	}

}

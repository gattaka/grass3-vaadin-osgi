package cz.gattserver.grass3.pg;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.mock.MockFileSystemService;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class PGRequestHandlerTest extends AbstractContextAwareTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private ConfigurationService configurationService;

	@Before
	public void init() {
		fileSystemService.init();
	}

	private Path prepareFS(FileSystem fs) throws IOException {
		Path rootDir = fs.getPath("/some/path/pg/root/");
		Files.createDirectories(rootDir);

		PGConfiguration conf = new PGConfiguration();
		conf.setRootDir(rootDir.toString());
		configurationService.saveConfiguration(conf);

		return rootDir;
	}

	@Test
	public void test() throws IOException {
		Path rootDir = prepareFS(fileSystemService.getFileSystem());
		Path testFile = Files.createFile(rootDir.resolve("testFile"));
		Files.write(testFile, new byte[] { 1, 1, 1 });

		Path file = new PGRequestHandler().getPath("testFile");
		assertTrue(Files.exists(file));
		assertEquals(3L, Files.size(file));
		assertEquals("testFile", file.getFileName().toString());
		assertEquals("/some/path/pg/root/testFile", file.toString());
	}

}

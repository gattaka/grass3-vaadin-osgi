package cz.gattserver.grass3.pg.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.mock.CoreMockService;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.modules.PGModule;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.model.domain.Photogallery;
import cz.gattserver.grass3.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.test.MockFileSystemService;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class PGServiceImplTest extends AbstractDBUnitTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private PGService pgService;

	@Autowired
	private PhotogalleryRepository photogalleryRepository;

	@Autowired
	private CoreMockService coreMockService;

	@Autowired
	private ContentNodeService contentNodeService;

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
	public void testLoadConfiguration() {
		PGConfiguration conf = new PGConfiguration();
		conf.setRootDir("test-root-dir");
		conf.setMiniaturesDir("test-mini-dir");
		conf.setPreviewsDir("test-prev-dir");
		conf.setSlideshowDir("test-slideshow-dir");
		configurationService.saveConfiguration(conf);

		conf = pgService.loadConfiguration();
		assertEquals("test-root-dir", conf.getRootDir());
		assertEquals("test-mini-dir", conf.getMiniaturesDir());
		assertEquals("test-prev-dir", conf.getPreviewsDir());
		assertEquals("test-slideshow-dir", conf.getSlideshowDir());
	}

	@Test
	public void testStoreConfiguration() {
		PGConfiguration conf = new PGConfiguration();
		conf.setRootDir("test-root-dir");
		conf.setMiniaturesDir("test-mini-dir");
		conf.setPreviewsDir("test-prev-dir");
		conf.setSlideshowDir("test-slideshow-dir");
		pgService.storeConfiguration(conf);

		configurationService.loadConfiguration(conf);
		assertEquals("test-root-dir", conf.getRootDir());
		assertEquals("test-mini-dir", conf.getMiniaturesDir());
		assertEquals("test-prev-dir", conf.getPreviewsDir());
		assertEquals("test-slideshow-dir", conf.getSlideshowDir());
	}

	@Test
	public void testDeletePhotogallery() throws IOException {
		Path root = prepareFS(fileSystemService.getFileSystem());
		Path galleryDir = root.resolve("test");
		Path testDir = galleryDir.resolve("deep/file/deletions");
		Files.createDirectories(testDir);

		Path testFile = testDir.getParent().resolve("testFile1");
		Files.createFile(testFile);

		Path testFile2 = testDir.resolve("testFile2");
		Files.createFile(testFile2);

		assertTrue(Files.exists(galleryDir));
		assertTrue(Files.exists(testDir));
		assertTrue(Files.exists(testFile));
		assertTrue(Files.exists(testFile2));

		Photogallery photogallery = new Photogallery();
		photogallery.setPhotogalleryPath(galleryDir.getFileName().toString());
		photogallery = photogalleryRepository.save(photogallery);
		assertNotNull(photogallery);
		
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);
		Long contentNodeId1 = contentNodeService.save(PGModule.ID, photogallery.getId(),
				"testGallery", null, true, nodeId1, userId1, false, LocalDateTime.now(),
				null);
		
		ContentNode contentNode = new ContentNode();
		contentNode.setId(contentNodeId1);
		photogallery.setContentNode(contentNode);
		photogallery = photogalleryRepository.save(photogallery);

		pgService.deletePhotogallery(photogallery.getId());

		assertFalse(Files.exists(galleryDir));
		assertFalse(Files.exists(testDir));
		assertFalse(Files.exists(testFile));
		assertFalse(Files.exists(testFile2));

		photogallery = photogalleryRepository.findOne(photogallery.getId());
		assertNull(photogallery);
	}

}

package cz.gattserver.grass3.pg.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.mock.CoreMockService;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.modules.PGModule;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;
import cz.gattserver.grass3.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.test.MockFileSystemService;
import cz.gattserver.grass3.pg.test.PGMockEventsHandler;
import cz.gattserver.grass3.pg.util.ImageComparator;
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

	@Autowired
	private EventBus eventBus;

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
		Long contentNodeId1 = contentNodeService.save(PGModule.ID, photogallery.getId(), "Test galerie", null, true,
				nodeId1, userId1, false, LocalDateTime.now(), null);

		ContentNode contentNode = new ContentNode();
		contentNode.setId(contentNodeId1);
		photogallery.setContentNode(contentNode);
		photogallery = photogalleryRepository.save(photogallery);

		pgService.deletePhotogallery(photogallery.getId());

		assertFalse(Files.exists(galleryDir));
		assertFalse(Files.exists(testDir));
		assertFalse(Files.exists(testFile));
		assertFalse(Files.exists(testFile2));
		assertTrue(Files.exists(root));

		photogallery = photogalleryRepository.findOne(photogallery.getId());
		assertNull(photogallery);
	}

	@Test
	public void testSavePhotogallery() throws IOException, InterruptedException, ExecutionException {
		Path root = prepareFS(fileSystemService.getFileSystem());
		Path galleryDir = root.resolve("testGallery");
		Files.createDirectories(galleryDir);

		Path animatedSmallFile = galleryDir.resolve("01.gif");
		Files.copy(this.getClass().getResourceAsStream("animatedSmall.gif"), animatedSmallFile);
		assertTrue(Files.exists(animatedSmallFile));

		Path largeFile = galleryDir.resolve("02.jpg");
		Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
		assertTrue(Files.exists(largeFile));

		Path smallFile = galleryDir.resolve("03.jpg");
		Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
		assertTrue(Files.exists(smallFile));

		Path orientedLargeFile = galleryDir.resolve("04.jpg");
		Files.copy(this.getClass().getResourceAsStream("orientedLarge.jpg"), orientedLargeFile);
		assertTrue(Files.exists(orientedLargeFile));

		Path x264MP4File = galleryDir.resolve("05.mp4");
		Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
		assertTrue(Files.exists(x264MP4File));

		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);
		PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
				null, true);

		PGMockEventsHandler eventsHandler = new PGMockEventsHandler();
		eventBus.subscribe(eventsHandler);
		CompletableFuture<PGMockEventsHandler> future = eventsHandler.expectEvent();

		pgService.savePhotogallery(payloadTO, nodeId1, userId1, LocalDateTime.now());

		future.get();

		assertTrue(eventsHandler.success);
		assertNotNull(eventsHandler.pgId);

		eventBus.unsubscribe(eventsHandler);

		PGConfiguration conf = new PGConfiguration();
		configurationService.loadConfiguration(conf);

		double acceptableDifference = 0.05; // 5%

		// Animated small
		Path animatedSmallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("01.gif");
		Path animatedSmallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("01.gif");
		assertTrue(Files.exists(animatedSmallFile));
		assertTrue(Files.exists(animatedSmallMiniature));
		assertFalse(Files.exists(animatedSmallSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(animatedSmallMiniature),
				this.getClass().getResourceAsStream("animatedSmallMiniature.gif")) < acceptableDifference);

		// Large
		Path largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
		Path largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
		assertTrue(Files.exists(largeFile));
		assertTrue(Files.exists(largeMiniature));
		assertTrue(Files.exists(largeSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeMiniature),
				this.getClass().getResourceAsStream("largeMiniature.jpg")) < acceptableDifference);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeSlideshow),
				this.getClass().getResourceAsStream("largeSlideshow.jpg")) < acceptableDifference);

		// Small
		Path smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
		Path smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
		assertTrue(Files.exists(smallFile));
		assertTrue(Files.exists(smallMiniature));
		assertFalse(Files.exists(smallSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(smallMiniature),
				this.getClass().getResourceAsStream("smallMiniature.jpg")) < acceptableDifference);

		// Oriented large
		Path orientedLargeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("04.jpg");
		Path orientedLargeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("04.jpg");
		assertTrue(Files.exists(orientedLargeFile));
		assertTrue(Files.exists(orientedLargeMiniature));
		assertTrue(Files.exists(orientedLargeSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeMiniature),
				this.getClass().getResourceAsStream("orientedLargeMiniature.jpg")) < acceptableDifference);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeSlideshow),
				this.getClass().getResourceAsStream("orientedLargeSlideshow.jpg")) < acceptableDifference);

		// X264 MP4
		Path x264MP4Preview = galleryDir.resolve(conf.getPreviewsDir()).resolve("05.mp4.png");
		assertTrue(Files.exists(x264MP4File));
		assertTrue(Files.exists(x264MP4Preview));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(x264MP4Preview),
				this.getClass().getResourceAsStream("x264Preview.png")) < acceptableDifference);
	}

	@Test
	public void testModifyPhotogallery() throws IOException, InterruptedException, ExecutionException {
		Path root = prepareFS(fileSystemService.getFileSystem());
		Path galleryDir = root.resolve("testGallery");
		Files.createDirectories(galleryDir);

		Path animatedSmallFile = galleryDir.resolve("01.gif");
		Files.copy(this.getClass().getResourceAsStream("animatedSmall.gif"), animatedSmallFile);
		assertTrue(Files.exists(animatedSmallFile));

		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);
		PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
				null, true);

		PGMockEventsHandler eventsHandler = new PGMockEventsHandler();
		eventBus.subscribe(eventsHandler);
		CompletableFuture<PGMockEventsHandler> future = eventsHandler.expectEvent();

		pgService.savePhotogallery(payloadTO, nodeId1, userId1, LocalDateTime.now());

		future.get();

		assertTrue(eventsHandler.success);
		assertNotNull(eventsHandler.pgId);
		long galleryId = eventsHandler.pgId;

		eventBus.unsubscribe(eventsHandler);

		Path largeFile = galleryDir.resolve("02.jpg");
		Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
		assertTrue(Files.exists(largeFile));

		Path smallFile = galleryDir.resolve("03.jpg");
		Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
		assertTrue(Files.exists(smallFile));

		Path orientedLargeFile = galleryDir.resolve("04.jpg");
		Files.copy(this.getClass().getResourceAsStream("orientedLarge.jpg"), orientedLargeFile);
		assertTrue(Files.exists(orientedLargeFile));

		eventsHandler = new PGMockEventsHandler();
		eventBus.subscribe(eventsHandler);
		future = eventsHandler.expectEvent();

		pgService.modifyPhotogallery(galleryId, payloadTO, LocalDateTime.now());

		future.get();

		assertTrue(eventsHandler.success);

		PGConfiguration conf = new PGConfiguration();
		configurationService.loadConfiguration(conf);

		double acceptableDifference = 0.05; // 5%

		// Animated small
		Path animatedSmallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("01.gif");
		Path animatedSmallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("01.gif");
		assertTrue(Files.exists(animatedSmallFile));
		assertTrue(Files.exists(animatedSmallMiniature));
		assertFalse(Files.exists(animatedSmallSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(animatedSmallMiniature),
				this.getClass().getResourceAsStream("animatedSmallMiniature.gif")) < acceptableDifference);

		// Large
		Path largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
		Path largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
		assertTrue(Files.exists(largeFile));
		assertTrue(Files.exists(largeMiniature));
		assertTrue(Files.exists(largeSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeMiniature),
				this.getClass().getResourceAsStream("largeMiniature.jpg")) < acceptableDifference);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeSlideshow),
				this.getClass().getResourceAsStream("largeSlideshow.jpg")) < acceptableDifference);

		// Small
		Path smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
		Path smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
		assertTrue(Files.exists(smallFile));
		assertTrue(Files.exists(smallMiniature));
		assertFalse(Files.exists(smallSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(smallMiniature),
				this.getClass().getResourceAsStream("smallMiniature.jpg")) < acceptableDifference);

		// Oriented large
		Path orientedLargeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("04.jpg");
		Path orientedLargeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("04.jpg");
		assertTrue(Files.exists(orientedLargeFile));
		assertTrue(Files.exists(orientedLargeMiniature));
		assertTrue(Files.exists(orientedLargeSlideshow));
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeMiniature),
				this.getClass().getResourceAsStream("orientedLargeMiniature.jpg")) < acceptableDifference);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeSlideshow),
				this.getClass().getResourceAsStream("orientedLargeSlideshow.jpg")) < acceptableDifference);
	}

}

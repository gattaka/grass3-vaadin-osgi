package cz.gattserver.grass3.hw.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.mock.MockFileSystemService;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;
import cz.gattserver.grass3.ui.util.ImageComparator;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class HWServiceImplTest extends AbstractDBUnitTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private HWService hwService;

	@Autowired
	private ConfigurationService configurationService;

	@Before
	public void init() {
		fileSystemService.init();
	}

	private Path prepareFS(FileSystem fs) throws IOException {
		Path rootDir = fs.getPath("/some/path/hw/root/");
		Files.createDirectories(rootDir);

		HWConfiguration conf = new HWConfiguration();
		conf.setRootDir(rootDir.toString());
		configurationService.saveConfiguration(conf);

		return rootDir;
	}

	/*
	 * Images
	 */

	@Test
	public void saveImagesFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage.jpg", itemTO);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getImagesDir()).resolve("testImage.jpg");
		assertTrue(Files.exists(smallFile));
	}

	@Test
	public void getHWItemImagesFiles() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);

		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage1.jpg", itemTO);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage2.jpg", itemTO);

		List<HWItemFileTO> files = hwService.getHWItemImagesFiles(itemTO);
		assertEquals(2, files.size());
		assertEquals("testImage1.jpg", files.get(0).getName());
		assertEquals("testImage2.jpg", files.get(1).getName());
	}

	@Test
	public void getHWItemImagesFileInputStream() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage.jpg", itemTO);

		InputStream is = hwService.getHWItemImagesFileInputStream(itemTO, "testImage.jpg");
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("large.jpg"), is));
	}

	@Test
	public void deleteHWItemImagesFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage.jpg", itemTO);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getImagesDir()).resolve("testImage.jpg");
		assertTrue(Files.exists(smallFile));

		hwService.deleteHWItemImagesFile(itemTO, "testImage.jpg");

		assertFalse(Files.exists(smallFile));
	}

	/*
	 * Documents
	 */

	@Test
	public void saveDocumentsFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc.jpg", itemTO);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getDocumentsDir()).resolve("testDoc.jpg");
		assertTrue(Files.exists(smallFile));
	}

	@Test
	public void getHWItemDocumentsFiles() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);

		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc1.jpg", itemTO);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc2.jpg", itemTO);

		List<HWItemFileTO> files = hwService.getHWItemDocumentsFiles(itemTO);
		assertEquals(2, files.size());
		assertEquals("testDoc1.jpg", files.get(0).getName());
		assertEquals("testDoc2.jpg", files.get(1).getName());
	}

	@Test
	public void getHWItemDocumentsFileInputStream() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc.jpg", itemTO);

		InputStream is = hwService.getHWItemDocumentsFileInputStream(itemTO, "testDoc.jpg");
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("large.jpg"), is));
	}

	@Test
	public void deleteHWItemDocumentsFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc.jpg", itemTO);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getDocumentsDir()).resolve("testDoc.jpg");
		assertTrue(Files.exists(smallFile));

		hwService.deleteHWItemDocumentsFile(itemTO, "testDoc.jpg");

		assertFalse(Files.exists(smallFile));
	}

	/*
	 * Icons
	 */

	@Test
	public void createHWItemIconOutputStream() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");
		Files.createDirectories(hwDir);

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		OutputStream os = hwService.createHWItemIconOutputStream("testIcon.jpg", itemTO);
		IOUtils.copy(this.getClass().getResourceAsStream("large.jpg"), os);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve("icon.jpg");
		assertTrue(Files.exists(smallFile));
	}

	@Test
	public void getHWItemIconFileInputStream() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");
		Files.createDirectories(hwDir);

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		OutputStream os = hwService.createHWItemIconOutputStream("testIcon.jpg", itemTO);
		IOUtils.copy(this.getClass().getResourceAsStream("large.jpg"), os);

		InputStream is = hwService.getHWItemIconFileInputStream(itemTO);
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("large.jpg"), is));
	}

	@Test
	public void deleteHWItemIconFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");
		Files.createDirectories(hwDir);

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		OutputStream os = hwService.createHWItemIconOutputStream("testIcon.jpg", itemTO);
		IOUtils.copy(this.getClass().getResourceAsStream("large.jpg"), os);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve("icon.jpg");
		assertTrue(Files.exists(smallFile));

		hwService.deleteHWItemIconFile(itemTO);
		assertFalse(Files.exists(smallFile));
	}

}

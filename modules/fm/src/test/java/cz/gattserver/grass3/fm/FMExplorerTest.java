package cz.gattserver.grass3.fm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.FileProcessState;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.fm.interfaces.FMItemTO;
import cz.gattserver.grass3.fm.test.MockFileSystemService;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class FMExplorerTest extends AbstractContextAwareTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private ConfigurationService configurationService;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void init() {
		fileSystemService.init();
	}

	private Path prepareFS(FileSystem fs) throws IOException {
		Path rootDir = fs.getPath("/some/path/fm/root/");
		Files.createDirectories(rootDir);

		FMConfiguration fmc = new FMConfiguration();
		fmc.setRootDir(rootDir.toString());
		configurationService.saveConfiguration(fmc);

		return rootDir;
	}

	@Test
	public void test() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);

		FMExplorer explorer = new FMExplorer(fs);
		assertEquals(FileProcessState.NOT_VALID, explorer.goToDir("../../fm"));

		explorer = new FMExplorer(fs);
		assertEquals(FileProcessState.MISSING, explorer.goToDir("sub1"));

		String subDirName = "sub2";
		Path subDir = rootDir.resolve(subDirName);
		Files.createDirectory(subDir);
		explorer = new FMExplorer(fs);
		assertEquals(FileProcessState.SUCCESS, explorer.goToDir(subDirName));
	}

	@Test
	public void testCreateDir() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);

		FMExplorer explorer = new FMExplorer(fs);
		String newDirName = "newDir";
		explorer.createNewDir(newDirName);

		Path newDir = rootDir.resolve(newDirName);
		assertTrue(Files.exists(newDir));
	}

	@Test
	public void testDeleteFile() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer(fs);
		explorer.goToDir("subDir1");

		assertEquals(FileProcessState.NOT_VALID, explorer.deleteFile("../.."));
		assertTrue(Files.exists(subDir));

		assertEquals(FileProcessState.MISSING, explorer.deleteFile("nonexisting"));
		assertTrue(Files.exists(subDir));

		assertEquals(FileProcessState.SUCCESS, explorer.deleteFile("subDir2"));
		assertFalse(Files.exists(subDir));
	}

	@Test
	public void testRenameFile() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer(fs);
		explorer.goToDir("subDir1");

		assertEquals(FileProcessState.NOT_VALID, explorer.renameFile("subDir2", "../../../sssDir"));
		assertTrue(Files.exists(subDir));

		assertEquals(FileProcessState.ALREADY_EXISTS, explorer.renameFile("subDir2", "../subDir1"));
		assertTrue(Files.exists(subDir));

		assertEquals(FileProcessState.SUCCESS, explorer.renameFile("subDir2", "sssDir"));
		assertTrue(Files.exists(rootDir.resolve("subDir1").resolve("sssDir")));
	}

	@Test
	public void testGoToDir() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer(fs);
		assertEquals(FileProcessState.MISSING, explorer.goToDir("nonExisting"));
		assertEquals(FileProcessState.NOT_VALID, explorer.goToDir("../../test"));
		assertEquals(FileProcessState.SUCCESS, explorer.goToDir("subDir1/subDir2"));
	}

	@Test
	public void testGoToDirFromCurrentDir() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer(fs);
		assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir1"));
		assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir2"));
	}

	@Test
	public void testGoToDirByURL() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer(fs);
		assertEquals(FileProcessState.SUCCESS,
				explorer.goToDirByURL("http://test/web", "fm-test", "http://test/web/fm-test/subDir1"));
		assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir2"));
	}

	@Test
	public void testGetCurrentURL() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer(fs);
		assertEquals("http://test/web/fm-test/", explorer.getCurrentURL("http://test/web", "fm-test"));
		explorer.goToDir("subDir1/subDir2");
		assertEquals("http://test/web/fm-test/subDir1/subDir2", explorer.getCurrentURL("http://test/web", "fm-test"));
	}

	@Test
	public void testListing() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName2 = "subDir1/subDir2";
		Path subDir2 = Files.createDirectories(rootDir.resolve(subDirName2));
		Path subDir3 = Files.createDirectories(subDir2.resolve("subDir3"));
		Path file1 = Files.createFile(subDir2.resolve("file1"));
		Path file2 = Files.createFile(subDir2.resolve("file2"));
		Path file3 = Files.createFile(subDir3.resolve("file3"));
		Path file4 = Files.createFile(subDir3.resolve("file4"));

		Files.write(file1, new byte[] { 1, 1, 1 });
		Files.write(file2, new byte[] { 1 });
		Files.write(file3, new byte[] { 1, 1, 1, 1 });
		Files.write(file4, new byte[] { 1, 1, 1, 1, 1 });

		FMExplorer explorer = new FMExplorer(fs);
		explorer.goToDir(subDirName2);

		assertEquals(4, explorer.listCount());

		Iterator<FMItemTO> it = explorer.listing(0, 10).iterator();
		FMItemTO item = it.next();
		assertEquals("..", item.getName());
		assertNotNull(item.getLastModified());
		assertEquals("", item.getSize());
		assertTrue(item.isDirectory());
		assertNull(item.getPathFromFMRoot());

		item = it.next();
		assertEquals("subDir3", item.getName());
		assertNotNull(item.getLastModified());
		assertEquals("9 B", item.getSize());
		assertTrue(item.isDirectory());
		assertNull(item.getPathFromFMRoot());

		item = it.next();
		assertEquals("file1", item.getName());
		assertNotNull(item.getLastModified());
		assertEquals("3 B", item.getSize());
		assertFalse(item.isDirectory());
		assertNull(item.getPathFromFMRoot());

		item = it.next();
		assertEquals("file2", item.getName());
		assertNotNull(item.getLastModified());
		assertEquals("1 B", item.getSize());
		assertFalse(item.isDirectory());
		assertNull(item.getPathFromFMRoot());
	}

	@Test
	public void testBreadcrumb() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName2 = "subDir1/subDir2";
		Path subDir2 = Files.createDirectories(rootDir.resolve(subDirName2));
		Files.createDirectories(subDir2.resolve("subDir3"));
		Files.createFile(subDir2.resolve("file1"));
		Files.createFile(subDir2.resolve("file2"));

		FMExplorer explorer = new FMExplorer(fs);
		explorer.goToDir(subDirName2);

		Iterator<FMItemTO> it = explorer.getBreadcrumbChunks().iterator();
		FMItemTO item = it.next();
		assertEquals("subDir2", item.getName());
		assertEquals("subDir1/subDir2", item.getPathFromFMRoot());

		item = it.next();
		assertEquals("subDir1", item.getName());
		assertEquals("subDir1", item.getPathFromFMRoot());

		item = it.next();
		assertEquals("/", item.getName());
		assertEquals("", item.getPathFromFMRoot());
	}

	@Test
	public void test_failNullFS() throws IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Filesystem nesmí být null");
		new FMExplorer(null);
	}

	@Test
	public void test_failRoot() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();

		Path rootDir = fs.getPath("/some/path/fm/root/");

		FMConfiguration fmc = new FMConfiguration();
		fmc.setRootDir(rootDir.toString());
		configurationService.saveConfiguration(fmc);

		exception.expect(GrassPageException.class);
		exception.expectMessage("Error: 500, Kořenový adresář FM modulu musí existovat");
		new FMExplorer(fs);
	}

}

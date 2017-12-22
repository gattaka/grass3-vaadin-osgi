package cz.gattserver.grass3.fm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.FileProcessState;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.fm.interfaces.PathChunkTO;
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

		FMExplorer explorer = new FMExplorer("../../fm", fs);
		assertEquals(FileProcessState.NOT_VALID, explorer.getState());

		explorer = new FMExplorer("sub1", fs);
		assertEquals(FileProcessState.MISSING, explorer.getState());

		String subDirName = "sub2";
		Path subDir = rootDir.resolve(subDirName);
		Files.createDirectory(subDir);
		explorer = new FMExplorer(subDirName, fs);
		assertEquals(FileProcessState.SUCCESS, explorer.getState());
	}

	@Test
	public void testCreateDir() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);

		FMExplorer explorer = new FMExplorer("", fs);
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

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals(FileProcessState.SUCCESS, explorer.deleteFile(subDir));
		assertFalse(Files.exists(subDir));
	}

	@Test
	public void testDeleteFile_NotValid() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals(FileProcessState.NOT_VALID, explorer.deleteFile(rootDir.resolve("..")));
		assertTrue(Files.exists(subDir));
	}

	@Test
	public void testDeleteFile_Missing() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals(FileProcessState.MISSING, explorer.deleteFile(subDir.resolve("nonexisting")));
		assertTrue(Files.exists(subDir));
	}

	@Test
	public void testRenameFile() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals(FileProcessState.SUCCESS, explorer.renameFile(subDir, "sssDir"));
		assertTrue(Files.exists(rootDir.resolve("subDir1").resolve("sssDir")));
	}

	@Test
	public void testRenameFile_NotValid() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals(FileProcessState.NOT_VALID, explorer.renameFile(subDir, "../../../sssDir"));
		assertTrue(Files.exists(subDir));
	}

	@Test
	public void testRenameFile_Missing() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals(FileProcessState.ALREADY_EXISTS, explorer.renameFile(subDir, "../subDir1"));
		assertTrue(Files.exists(subDir));
	}

	@Test
	public void testGotoDir() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName = "subDir1/subDir2";
		Files.createDirectories(rootDir.resolve(subDirName));

		FMExplorer explorer = new FMExplorer("subDir1", fs);
		assertEquals("/some/path/fm/root", explorer.getRootPath().toString());
		assertEquals("/some/path/fm/root/subDir1", explorer.getCurrentAbsolutePath().toString());
		assertEquals("subDir1", explorer.getCurrentRelativePath().toString());

		assertEquals(FileProcessState.MISSING, explorer.tryGotoDir("nonExisting"));
		assertEquals("/some/path/fm/root", explorer.getRootPath().toString());
		assertEquals("/some/path/fm/root/subDir1", explorer.getCurrentAbsolutePath().toString());
		assertEquals("subDir1", explorer.getCurrentRelativePath().toString());

		assertEquals(FileProcessState.NOT_VALID, explorer.tryGotoDir("../../test"));
		assertEquals("/some/path/fm/root", explorer.getRootPath().toString());
		assertEquals("/some/path/fm/root/subDir1", explorer.getCurrentAbsolutePath().toString());
		assertEquals("subDir1", explorer.getCurrentRelativePath().toString());

		assertEquals(FileProcessState.SUCCESS, explorer.tryGotoDir("subDir1/subDir2"));
		assertEquals("/some/path/fm/root", explorer.getRootPath().toString());
		assertEquals("/some/path/fm/root/subDir1/subDir2", explorer.getCurrentAbsolutePath().toString());
		assertEquals("subDir1/subDir2", explorer.getCurrentRelativePath().toString());
	}

	@Test
	public void testListing() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName2 = "subDir1/subDir2";
		Path subDir2 = Files.createDirectories(rootDir.resolve(subDirName2));
		Files.createDirectories(subDir2.resolve("subDir3"));
		Files.createFile(subDir2.resolve("file1"));
		Files.createFile(subDir2.resolve("file2"));

		FMExplorer explorer = new FMExplorer(subDirName2, fs);

		assertEquals(4, explorer.listCount());

		Iterator<Path> it = explorer.listing(0, 10).iterator();
		assertEquals("..", it.next().toString());
		assertEquals("/some/path/fm/root/subDir1/subDir2/subDir3", it.next().toString());
		assertEquals("/some/path/fm/root/subDir1/subDir2/file1", it.next().toString());
		assertEquals("/some/path/fm/root/subDir1/subDir2/file2", it.next().toString());
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

		FMExplorer explorer = new FMExplorer(subDirName2, fs);

		List<PathChunkTO> chunks = explorer.getBreadcrumbChunks();
		assertEquals("subDir2", chunks.get(0).getName());
		assertEquals("subDir1/subDir2", chunks.get(0).getPath().toString());
		assertEquals("subDir1", chunks.get(1).getName());
		assertEquals("subDir1", chunks.get(1).getPath().toString());
		assertEquals("/", chunks.get(2).getName());
		assertEquals("", chunks.get(2).getPath().toString());
	}

	@Test
	public void testDeepDirSize() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();
		Path rootDir = prepareFS(fs);
		String subDirName2 = "subDir1/subDir2";
		Path subDir2 = Files.createDirectories(rootDir.resolve(subDirName2));
		Path subDir3 = Files.createDirectories(subDir2.resolve("subDir3"));
		Path file1 = Files.createFile(subDir2.resolve("file1"));
		Path file2 = Files.createFile(subDir2.resolve("file2"));
		Path file3 = Files.createFile(subDir3.resolve("file3"));

		Files.write(file1, new byte[] { 1, 1, 1 });
		Files.write(file2, new byte[] { 1 });
		Files.write(file3, new byte[] { 1, 1, 1, 1 });

		FMExplorer explorer = new FMExplorer(subDirName2, fs);

		assertEquals(3L, explorer.getDeepDirSize(file1).longValue());
		assertEquals(1L, explorer.getDeepDirSize(file2).longValue());
		assertNull(explorer.getDeepDirSize(rootDir.resolve("..")));
		assertEquals(4L, explorer.getDeepDirSize(subDir3).longValue());

		assertEquals(8L, explorer.getDeepDirSize(fs.getPath("")).longValue());

		exception.expect(IOException.class);
		assertNull(explorer.getDeepDirSize(fs.getPath("nonexisting")));
	}

	@Test
	public void test_failNullRelativePath() throws IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("RelativePath nesmí být null");
		new FMExplorer(null, null);
	}

	@Test
	public void test_failNullFS() throws IOException {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Filesystem nesmí být null");
		new FMExplorer("", null);
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
		new FMExplorer("sub1", fs);
	}

}

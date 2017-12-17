package cz.gattserver.grass3.fm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.FMExplorer.FileProcessState;
import cz.gattserver.grass3.fm.config.FMConfiguration;
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

	@Test
	public void test() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();

		Path rootDir = fs.getPath("/some/path/fm/root/");
		Files.createDirectories(rootDir);

		Path tmpDir = fs.getPath("/some/path/fm/tmp/");
		Files.createDirectories(tmpDir);

		FMConfiguration fmc = new FMConfiguration();
		fmc.setRootDir(rootDir.toString());
		fmc.setTmpDir(tmpDir.toString());
		configurationService.saveConfiguration(fmc);

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
	public void test_failRoot() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();

		Path rootDir = fs.getPath("/some/path/fm/root/");

		Path tmpDir = fs.getPath("/some/path/fm/tmp/");
		Files.createDirectories(tmpDir);

		FMConfiguration fmc = new FMConfiguration();
		fmc.setRootDir(rootDir.toString());
		fmc.setTmpDir(tmpDir.toString());
		configurationService.saveConfiguration(fmc);

		exception.expect(GrassPageException.class);
		exception.expectMessage("Error: 500, Kořenový adresář FM modulu musí být existující absolutní cesta");
		new FMExplorer("sub1", fs);
	}

	@Test
	public void test_failTmp() throws IOException {
		FileSystem fs = fileSystemService.getFileSystem();

		Path rootDir = fs.getPath("/some/path/fm/root/");
		Files.createDirectories(rootDir);

		Path tmpDir = fs.getPath("/some/path/fm/tmp/");

		FMConfiguration fmc = new FMConfiguration();
		fmc.setRootDir(rootDir.toString());
		fmc.setTmpDir(tmpDir.toString());
		configurationService.saveConfiguration(fmc);

		exception.expect(GrassPageException.class);
		exception.expectMessage("Error: 500, Dočasný adresář FM modulu musí být existující absolutní cesta");
		new FMExplorer("sub1", fs);
	}

}

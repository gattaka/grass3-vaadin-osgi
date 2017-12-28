package cz.gattserver.grass3.pg.test;

import java.nio.file.FileSystem;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import cz.gattserver.grass3.services.FileSystemService;

@Primary
@Component(value = "MockFileSystemService")
public class MockFileSystemService implements FileSystemService {

	private FileSystem fileSystem;

	@PostConstruct
	public void init() {
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
	}

	@Override
	public FileSystem getFileSystem() {
		return fileSystem;
	}

}

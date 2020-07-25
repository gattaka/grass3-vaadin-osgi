package cz.gattserver.grass3.services.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.FileSystemService;

@Component
public class FileSystemImpl implements FileSystemService {

	@Override
	public FileSystem getFileSystem() {
		return FileSystems.getDefault();
	}

	@Override
	public FileSystem newZipFileSystem(Path path, boolean create) throws IOException {
		final Map<String, String> env = new HashMap<>();
		if (create)
			env.put("create", "true");
		return FileSystems.newFileSystem(URI.create("jar:" + path.toUri()), env);
	}

	@Override
	public Path createTmpDir(String name) throws IOException {
		return Files.createTempDirectory(name);
	}
}

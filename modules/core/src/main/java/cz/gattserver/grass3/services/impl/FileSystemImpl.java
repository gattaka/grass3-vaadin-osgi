package cz.gattserver.grass3.services.impl;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.FileSystemService;

@Component
public class FileSystemImpl implements FileSystemService {

	@Override
	public FileSystem getFileSystem() {
		return FileSystems.getDefault();
	}

}

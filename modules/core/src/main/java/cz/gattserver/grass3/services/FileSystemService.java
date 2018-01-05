package cz.gattserver.grass3.services;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface FileSystemService {

	FileSystem getFileSystem();

	FileSystem newZipFileSystem(Path path, boolean create) throws IOException;
}

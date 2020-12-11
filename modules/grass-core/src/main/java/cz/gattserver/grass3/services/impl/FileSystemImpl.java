package cz.gattserver.grass3.services.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.FileSystemService;

@Component
public class FileSystemImpl implements FileSystemService {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemImpl.class);

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

	private Set<PosixFilePermission> createPerms() {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		return perms;
	}

	@Override
	public Path createTmpDir(String name) throws IOException {
		return Files.createTempDirectory(name);
	}

	@Override
	public Path grantPermissions(Path path) throws IOException {
		Set<PosixFilePermission> perms = createPerms();
		try {
			Files.setPosixFilePermissions(path, perms);
		} catch (IOException e) {
			logger.warn("Nezdařilo se nastavit práva na soubor", e);
		}
		return path;
	}

	private FileAttribute<Set<PosixFilePermission>> createPermsAttributes() {
		Set<PosixFilePermission> perms = createPerms();
		FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
		return fileAttributes;
	}

	@Override
	public void createDirectoriesWithPerms(Path path) throws IOException {
		Files.createDirectories(path, createPermsAttributes());
	}

	@Override
	public Path createDirectoryWithPerms(Path path) throws IOException {
		return Files.createDirectory(path, createPermsAttributes());
	}
}

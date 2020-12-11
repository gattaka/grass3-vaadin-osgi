package cz.gattserver.grass3.ui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

	public static Set<PosixFilePermission> createPerms() {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		return perms;
	}

	public static Path grantPermissions(Path path) throws IOException {
		Set<PosixFilePermission> perms = createPerms();
		Files.setPosixFilePermissions(path, perms);
		return path;
	}

	public static FileAttribute<Set<PosixFilePermission>> createPermsAttributes() {
		Set<PosixFilePermission> perms = createPerms();
		FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
		return fileAttributes;
	}

}

package cz.gattserver.grass3.ui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

	public static Path grantPermissions(Path path) throws IOException {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		Files.setPosixFilePermissions(path, perms);
		return path;
	}

}

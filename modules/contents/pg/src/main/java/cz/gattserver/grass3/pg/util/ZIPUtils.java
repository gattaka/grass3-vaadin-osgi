package cz.gattserver.grass3.pg.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ZIPUtils} based on
 * <ul>
 * <li><a href=
 * "http://fahdshariff.blogspot.cz/2011/08/java-7-working-with-zip-files.html">http://fahdshariff.blogspot.cz/2011/08/java-7-working-with-zip-files.html</a></li>
 * <li><a href=
 * "https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html">https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html</a></li>
 * </ul>
 * 
 * zvážit
 * http://www.pixeldonor.com/2013/oct/12/concurrent-zip-compression-java-nio/
 * 
 */
public class ZIPUtils {

	/**
	 * Returns a zip file system
	 * 
	 * @param zipFilename
	 *            to construct the file system from
	 * @param create
	 *            true if the zip file should be created
	 * @return a zip file system
	 * @throws IOException
	 */
	public static FileSystem createZipFileSystem(Path zipFile, boolean create) throws IOException {
		final URI uri = URI.create("jar:file:" + zipFile.toUri().getPath());
		final Map<String, String> env = new HashMap<>();
		if (create) {
			env.put("create", "true");
		}
		return FileSystems.newFileSystem(uri, env);
	}

}

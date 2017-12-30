package cz.gattserver.grass3.pg.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class MetadataExtractor {

	private MetadataExtractor() {
	}

	public static int readImageOrientationByDrewnoakes(Path imageFile) {
		Metadata metadata;
		int orientation = 1;
		try {
			metadata = ImageMetadataReader.readMetadata(Files.newInputStream(imageFile));
			Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);
			if (!directories.isEmpty()) {
				orientation = directories.iterator().next().getInt(ExifIFD0Directory.TAG_ORIENTATION);
			}
		} catch (Exception e) {
			throw new IllegalStateException("Metadata se nezdařilo přečíst", e);
		}
		return orientation;
	}

}

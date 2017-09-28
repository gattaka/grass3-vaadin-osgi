package cz.gattserver.grass3.pg.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

public class PGUtils {

	private static String getExtension(File file) {
		int dot = file.getName().lastIndexOf(".");
		if (dot <= 0 || file.getName().length() < 3)
			return "";
		return file.getName().substring(dot + 1);
	}

	public static boolean isSmallerThenMaxArea(File inputFile, int maxArea) throws IOException {
		BufferedImage image = ImageIO.read(inputFile);
		return image.getHeight() * image.getWidth() < maxArea;
	}

	public static int readImageOrientation(File imageFile) {
		Metadata metadata;
		int orientation = 1;
		try {
			metadata = ImageMetadataReader.readMetadata(imageFile);
			Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);
			if (directories.isEmpty() == false) {
				orientation = directories.iterator().next().getInt(ExifIFD0Directory.TAG_ORIENTATION);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return orientation;
	}

	public static BufferedImage resizeBufferedImage(BufferedImage image, int maxWidth, int maxHeight) {
		ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(maxWidth, maxHeight));
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		return resampleOp.filter(image, null);
	}

	public static boolean resizeAndRotateImageFile(File inputFile, File destinationFile, int maxWidth, int maxHeight)
			throws IOException {

		BufferedImage image = resizeBufferedImage(ImageIO.read(inputFile), maxWidth, maxHeight);

		int orientation = readImageOrientation(inputFile);
		if (orientation != 1) {

			AffineTransform transform = new AffineTransform();
			int transformedWidth = image.getWidth();
			int transformedHeight = image.getHeight();

			switch (orientation) {
			case 1:
				break;
			case 2: // Flip X
				transform.scale(-1.0, 1.0);
				transform.translate(-image.getWidth(), 0);
				break;
			case 3: // PI rotation
				transform.translate(image.getWidth(), image.getHeight());
				transform.rotate(Math.PI);
				break;
			case 4: // Flip Y
				transform.scale(1.0, -1.0);
				transform.translate(0, -image.getHeight());
				break;
			case 5: // - PI/2 and Flip X
				transform.rotate(-Math.PI / 2);
				transform.scale(-1.0, 1.0);
				transformedWidth = image.getHeight();
				transformedHeight = image.getWidth();
				break;
			case 6: // -PI/2 and -width
				transform.translate(image.getHeight(), 0);
				transform.rotate(Math.PI / 2);
				transformedWidth = image.getHeight();
				transformedHeight = image.getWidth();
				break;
			case 7: // PI/2 and Flip
				transform.scale(-1.0, 1.0);
				transform.translate(-image.getHeight(), 0);
				transform.translate(0, image.getWidth());
				transform.rotate(3 * Math.PI / 2);
				transformedWidth = image.getHeight();
				transformedHeight = image.getWidth();
				break;
			case 8: // PI / 2
				transform.translate(0, image.getWidth());
				transform.rotate(3 * Math.PI / 2);
				transformedWidth = image.getHeight();
				transformedHeight = image.getWidth();
				break;
			}

			BufferedImage temp = new BufferedImage(transformedWidth, transformedHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = temp.createGraphics();
			g2.transform(transform);
			g2.drawImage(image, 0, 0, Color.WHITE, null);
			g2.dispose();
			image = temp;
		}

		ImageIO.write(image, getExtension(inputFile), destinationFile);
		return true;
	}

	public static boolean isImage(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".jpg") || fileToExt.endsWith(".jpeg") || fileToExt.endsWith(".gif")
				|| fileToExt.endsWith(".png") || fileToExt.endsWith(".bmp");
	}

	public static boolean isVideo(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".mp4") || fileToExt.endsWith(".ogg") || fileToExt.endsWith(".webm")
				|| fileToExt.endsWith(".mov");
	}

}

package cz.gattserver.grass3.pg.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Dithering;

public class PGUtils {

	public static final int MINIATURE_SIZE = 150;

	private PGUtils() {
	}

	private static String getExtension(Path file) {
		String filename = file.getFileName().toString();
		int dot = filename.lastIndexOf('.');
		if (dot <= 0 || filename.length() < 3)
			return "";
		return filename.substring(dot + 1);
	}

	public static boolean isSmallerThenMaxArea(Path inputFile, int maxArea) throws IOException {
		BufferedImage image = ImageIO.read(Files.newInputStream(inputFile));
		return image.getHeight() * image.getWidth() < maxArea;
	}

	public static BufferedImage resizeBufferedImage(BufferedImage image) {
		return resizeBufferedImage(image, PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE);
	}

	public static BufferedImage resizeBufferedImage(BufferedImage image, int maxWidth, int maxHeight) {
		ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(maxWidth, maxHeight));
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		return resampleOp.filter(image, null);
	}

	public static void resizeAndRotateImageFile(Path inputFile, Path destinationFile) throws IOException {
		resizeAndRotateImageFile(inputFile, destinationFile, PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE);
	}

	public static void resizeAndRotateImageFile(Path inputFile, Path destinationFile, int maxWidth, int maxHeight)
			throws IOException {

		try (InputStream is = Files.newInputStream(inputFile);
				OutputStream os = Files.newOutputStream(destinationFile)) {
			Thumbnails.of(is).size(maxWidth, maxHeight).keepAspectRatio(true).determineOutputFormat()
					.dithering(Dithering.DISABLE).antialiasing(Antialiasing.ON).toOutputStream(os);
		}

		// BufferedImage image =
		// resizeBufferedImage(ImageIO.read(Files.newInputStream(inputFile)),
		// maxWidth, maxHeight);
		//
		// int orientation =
		// MetadataExtractor.readImageOrientationByDrewnoakes(inputFile);
		// if (orientation != 1) {
		//
		// AffineTransform transform = new AffineTransform();
		// int transformedWidth = image.getWidth();
		// int transformedHeight = image.getHeight();
		//
		// switch (orientation) {
		// case 1:
		// break;
		// case 2: // Flip X
		// transform.scale(-1.0, 1.0);
		// transform.translate(-image.getWidth(), 0);
		// break;
		// case 3: // PI rotation
		// transform.translate(image.getWidth(), image.getHeight());
		// transform.rotate(Math.PI);
		// break;
		// case 4: // Flip Y
		// transform.scale(1.0, -1.0);
		// transform.translate(0, -image.getHeight());
		// break;
		// case 5: // - PI/2 and Flip X
		// transform.rotate(-Math.PI / 2);
		// transform.scale(-1.0, 1.0);
		// transformedWidth = image.getHeight();
		// transformedHeight = image.getWidth();
		// break;
		// case 6: // -PI/2 and -width
		// transform.translate(image.getHeight(), 0);
		// transform.rotate(Math.PI / 2);
		// transformedWidth = image.getHeight();
		// transformedHeight = image.getWidth();
		// break;
		// case 7: // PI/2 and Flip
		// transform.scale(-1.0, 1.0);
		// transform.translate(-image.getHeight(), 0);
		// transform.translate(0, image.getWidth());
		// transform.rotate(3 * Math.PI / 2);
		// transformedWidth = image.getHeight();
		// transformedHeight = image.getWidth();
		// break;
		// case 8: // PI / 2
		// transform.translate(0, image.getWidth());
		// transform.rotate(3 * Math.PI / 2);
		// transformedWidth = image.getHeight();
		// transformedHeight = image.getWidth();
		// break;
		// default:
		// break;
		// }
		//
		// BufferedImage temp = new BufferedImage(transformedWidth,
		// transformedHeight, BufferedImage.TYPE_INT_RGB);
		// Graphics2D g2 = temp.createGraphics();
		// g2.transform(transform);
		// g2.drawImage(image, 0, 0, Color.WHITE, null);
		// g2.dispose();
		// image = temp;
		// }
		//
		// ImageIO.write(image, getExtension(inputFile),
		// Files.newOutputStream(destinationFile));
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o obrázek
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor obrázku
	 */
	public static boolean isImage(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".jpg") || fileToExt.endsWith(".jpeg") || fileToExt.endsWith(".gif")
				|| fileToExt.endsWith(".png") || fileToExt.endsWith(".bmp");
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o obrázek
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor obrázku
	 */
	public static boolean isImage(Path file) {
		return PGUtils.isImage(file.getFileName().toString());
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o video
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor videa
	 */
	public static boolean isVideo(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".mp4") || fileToExt.endsWith(".ogg") || fileToExt.endsWith(".webm")
				|| fileToExt.endsWith(".mov") || fileToExt.endsWith(".avi");
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o video
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor videa
	 */
	public static boolean isVideo(Path file) {
		return PGUtils.isVideo(file.getFileName().toString());
	}

}

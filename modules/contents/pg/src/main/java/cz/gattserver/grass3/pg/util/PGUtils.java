package cz.gattserver.grass3.pg.util;

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

	public static String getExtension(Path file) {
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
					.dithering(Dithering.DISABLE).outputQuality(1).antialiasing(Antialiasing.ON).toOutputStream(os);
		}
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

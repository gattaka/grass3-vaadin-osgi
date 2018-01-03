package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import net.coobird.thumbnailator.Thumbnails;

public class PGUtils {

	public static final int MINIATURE_SIZE = 150;

	private PGUtils() {
	}

	public static String getExtension(Path file) {
		String filename = file.getFileName().toString();
		int dot = filename.lastIndexOf('.');
		if (dot <= 0)
			return "";
		return filename.substring(dot + 1);
	}

	public static void resizeImage(Path inputFile, Path destinationFile) throws IOException {
		resizeImage(inputFile, destinationFile, PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE);
	}

	public static void resizeVideoPreviewImage(BufferedImage inputImage, Path destinationFile) throws IOException {
		try (OutputStream os = Files.newOutputStream(destinationFile)) {
			Thumbnails.of(inputImage).outputFormat("png").size(PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE)
					.toOutputStream(os);
		}
	}

	public static void resizeImage(Path inputFile, Path destinationFile, int maxWidth, int maxHeight)
			throws IOException {
		try (InputStream is = Files.newInputStream(inputFile);
				OutputStream os = Files.newOutputStream(destinationFile)) {
			Thumbnails.of(is).size(maxWidth, maxHeight).toOutputStream(os);
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

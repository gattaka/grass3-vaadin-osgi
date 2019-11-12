package cz.gattserver.grass3.pg.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;
import net.coobird.thumbnailator.Thumbnails;

public class PGUtils {

	private static Logger logger = LoggerFactory.getLogger(PGUtils.class);

	public static final int MINIATURE_SIZE = 150;
	public static final int SLIDESHOW_WIDTH = 900;
	public static final int SLIDESHOW_HEIGHT = 800;

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

	public static void createErrorPreview(String filename, Path destinationFile) {
		int h = MINIATURE_SIZE;
		int w = MINIATURE_SIZE;
		BufferedImage backgroundImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = backgroundImage.createGraphics();
		// https://stackoverflow.com/questions/4855847/problem-with-fillroundrect-seemingly-not-rendering-correctly
		bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bg.setColor(new Color(50, 50, 50));
		int wOffset = 10;
		int hOffset = 30;
		int corner = 25;
		bg.fillRoundRect(wOffset, hOffset, w - wOffset * 2, h - hOffset * 2, corner, corner);
		bg.setColor(new Color(150, 150, 150));
		int xc = w / 2 + 5;
		int yc = h / 2;
		int xr = 20;
		int yr = 20;
		bg.fillPolygon(new Polygon(new int[] { xc - xr, xc + xr, xc - xr }, new int[] { yc - yr, yc, yc + yr }, 3));
		try (OutputStream o = Files.newOutputStream(destinationFile)) {
			ImageIO.write(backgroundImage, "png", o);
		} catch (IOException e) {
			logger.error("Vytváření chybového náhledu videa {} se nezdařilo", destinationFile.getFileName().toString(),
					e);
		}
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
			if (inputFile.toString().toLowerCase().endsWith(".gif")) {
				GifImage gifImage = GifDecoder.read(is);
				BufferedImage image = gifImage.getFrame(0);
				Thumbnails.of(image).outputFormat("png").size(maxWidth, maxHeight).toOutputStream(os);
			} else if (inputFile.toString().toLowerCase().endsWith(".svg")) {
				// https://xmlgraphics.apache.org/batik/using/transcoder.html
				// https://stackoverflow.com/questions/42340833/convert-svg-image-to-png-in-java-by-servlet
				// https://stackoverflow.com/questions/45239099/apache-batik-no-writeadapter-is-available
				TranscoderInput input = new TranscoderInput(is);
				TranscoderOutput output = new TranscoderOutput(os);
				PNGTranscoder converter = new PNGTranscoder();
				converter.addTranscodingHint(JPEGTranscoder.KEY_MAX_WIDTH, new Float(maxWidth));
				converter.addTranscodingHint(JPEGTranscoder.KEY_MAX_HEIGHT, new Float(maxHeight));
				converter.transcode(input, output);
			} else {
				Thumbnails.of(is).outputFormat("png").size(maxWidth, maxHeight).toOutputStream(os);
			}
		} catch (TranscoderException e) {
			throw new IOException("SVG to PNG failed", e);
		}
	}

	public static BufferedImage getImageFromFile(Path inputFile) throws IOException {
		try (InputStream is = Files.newInputStream(inputFile)) {
			if (inputFile.toString().toLowerCase().endsWith(".gif")) {
				GifImage gifImage = GifDecoder.read(is);
				return gifImage.getFrame(0);
			} else {
				return ImageIO.read(is);
			}
		}
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o rasterový obrázek
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor
	 *         rasterového obrázku
	 */
	public static boolean isRasterImage(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".jpg") || fileToExt.endsWith(".jpeg") || fileToExt.endsWith(".gif")
				|| fileToExt.endsWith(".png") || fileToExt.endsWith(".bmp");
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o rasterový obrázek
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor
	 *         rasterového obrázku
	 */
	public static boolean isRasterImage(Path file) {
		return PGUtils.isRasterImage(file.getFileName().toString());
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o vektorový obrázek
	 * 
	 * @param filename
	 *            jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor
	 *         vektorového obrázku
	 */
	public static boolean isVectorImage(Path file) {
		return file.getFileName().toString().endsWith(".svg");
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

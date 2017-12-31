package cz.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageComparator {

	private ImageComparator() {
	}

	/**
	 * Porovnává natvrdo data. Bere tedy jako nerovnost i EXIF záznam navíc nebo
	 * jiný styl zápisu barev (paleta apod.)
	 */
	public static boolean isEqualAsFiles(InputStream input1, InputStream input2) throws IOException {
		if (!(input1 instanceof BufferedInputStream))
			input1 = new BufferedInputStream(input1);
		if (!(input2 instanceof BufferedInputStream))
			input2 = new BufferedInputStream(input2);
		int ch = input1.read();
		while (-1 != ch) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
		}

		int ch2 = input2.read();
		return (ch2 == -1);
	}

	/**
	 * Porovnává natvrdo data obrázku. Bere tedy jako nerovnost i jiný styl
	 * zápisu barev (paleta apod.)
	 */
	// https://stackoverflow.com/questions/8567905/how-to-compare-images-for-similarity-using-java
	public static boolean isEqualAsImageData(InputStream input1, InputStream input2) {
		try {
			// take buffer data from botm image files
			BufferedImage biA = ImageIO.read(input1);
			DataBuffer dbA = biA.getData().getDataBuffer();
			int sizeA = dbA.getSize();
			BufferedImage biB = ImageIO.read(input2);
			DataBuffer dbB = biB.getData().getDataBuffer();
			int sizeB = dbB.getSize();
			// compare data-buffer objects
			if (sizeA == sizeB) {
				for (int i = 0; i < sizeA; i++) {
					if (dbA.getElem(i) != dbB.getElem(i)) {
						return false;
					}
				}
				return true;
			} else
				return false;
		} catch (Exception e) {
			System.out.println("Failed to compare image files ...");
			return false;
		}
	}

	/**
	 * Porovnává obrázky dle barev. Nezvládá průhlednost. Dva různě kódované
	 * obrázky mají jinou "barvu" průhlednosti. Neprůhledné obrázky různě
	 * zapsané pozná, že jsou stejné.
	 */
	// https://www.geeksforgeeks.org/image-processing-java-set-14-comparison-two-images/
	public static double isEqualAsImagePixels(InputStream input1, InputStream input2) throws IOException {
		BufferedImage imgA = ImageIO.read(input1);
		BufferedImage imgB = ImageIO.read(input2);
		int width1 = imgA.getWidth();
		int width2 = imgB.getWidth();
		int height1 = imgA.getHeight();
		int height2 = imgB.getHeight();
		if ((width1 != width2) || (height1 != height2))
			return 1;
		else {
			long difference = 0;
			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					int rgbA = imgA.getRGB(x, y);
					int rgbB = imgB.getRGB(x, y);

					int redA = (rgbA >> 16) & 0xff;
					int greenA = (rgbA >> 8) & 0xff;
					int blueA = (rgbA) & 0xff;
					int redB = (rgbB >> 16) & 0xff;
					int greenB = (rgbB >> 8) & 0xff;
					int blueB = (rgbB) & 0xff;
					difference += Math.abs(redA - redB);
					difference += Math.abs(greenA - greenB);
					difference += Math.abs(blueA - blueB);
				}
			}

			// Total number of red pixels = width * height
			// Total number of blue pixels = width * height
			// Total number of green pixels = width * height
			// So total number of pixels = width * height * 3
			double total_pixels = width1 * height1 * 3;

			// Normalizing the value of different pixels
			// for accuracy(average pixels per color
			// component)
			double avg_different_pixels = difference / total_pixels;

			// There are 255 values of pixels in total
			double percentage = (avg_different_pixels / 255);
			return percentage;
		}
	}
}
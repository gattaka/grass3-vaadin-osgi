package org.myftp.gattserver.grass3.pg.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageUtils {

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

	public static boolean resizeImageFile(File inputFile, File destinationFile, int maxWidth, int maxHeight)
			throws IOException {

		BufferedImage image = ImageIO.read(inputFile);

		ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(maxWidth, maxHeight));
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		image = resampleOp.filter(image, null);

		ImageIO.write(image, getExtension(inputFile), destinationFile);
		return true;
	}

}

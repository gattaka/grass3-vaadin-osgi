package org.myftp.gattserver.grass3.pg.pages;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.vaadin.peter.imagescaler.ImageScaler;

import com.vaadin.server.FileResource;

public class ImageDetailWindow extends GrassSubWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	public ImageDetailWindow(File image) {
		super(image.getName());

		int width = 100;
		int height = 100;

		// pomalé
		// BufferedImage bimg;
		// try {
		// bimg = ImageIO.read(image);
		// width = bimg.getWidth();
		// height = bimg.getHeight();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// rychlé
		try {
			ImageInputStream in = ImageIO.createImageInputStream(image);
			try {
				final Iterator<ImageReader> readers = ImageIO
						.getImageReaders(in);
				if (readers.hasNext()) {
					ImageReader reader = readers.next();
					try {
						reader.setInput(in);
						width = reader.getWidth(0);
						height = reader.getHeight(0);
					} finally {
						reader.dispose();
					}
				}
			} finally {
				if (in != null)
					in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		ImageScaler scaler = new ImageScaler();
		scaler.setImage(new FileResource(image), width, height);
		// scaler.setRecalculateOnSizeChangeEnabled(true); // Optional
		scaler.setWidth("1000px");
		scaler.setHeight("800px");
		addComponent(scaler);

		// Embedded embedded = new Embedded(null, new FileResource(image));
		// addComponent(embedded);

		center();

	}

}

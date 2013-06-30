package org.myftp.gattserver.grass3.subwindows;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.vaadin.peter.imagescaler.ImageScaler;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Image;

public class ImageDetailSubwindow extends GrassSubWindow {

	private static final long serialVersionUID = 4123506060675738841L;

	/**
	 * Okno bude vytvořeno přímo s připraveným popiskem
	 * 
	 * @param label
	 *            popisek okna
	 */
	public ImageDetailSubwindow(String description, Resource imageResource) {
		super(description);

		// TODO kliknutím se otevře plná velikost v novém tabu
		Image img = new Image(null, imageResource);
		addComponent(img);

		center();

	}

	public ImageDetailSubwindow(String description, File file) {
		super(description);

		// TODO kliknutím se otevře plná velikost v novém tabu

		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int width = bimg.getWidth();
		int height = bimg.getHeight();

		Image img = new Image(null, new FileResource(file)) {
			private static final long serialVersionUID = 6960804324956187486L;

			@Override
			public boolean handleConnectorRequest(VaadinRequest request,
					VaadinResponse response, String path) throws IOException {
				response.setHeader("Cache-Control", "private,no-cache,no-store");
				return super.handleConnectorRequest(request, response, path);
			}

		};
		addComponent(img);
		setWidth((width + 40) + "px");
		setHeight((height + 40) + "px");

		center();

	}

}

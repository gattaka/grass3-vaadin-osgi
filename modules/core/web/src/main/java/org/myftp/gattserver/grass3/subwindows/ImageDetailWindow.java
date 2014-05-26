package org.myftp.gattserver.grass3.subwindows;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

public class ImageDetailWindow extends GrassWindow {

	private static final long serialVersionUID = 4123506060675738841L;

	private static final int WINDOWPADDING = 45;
	private static final int WINDOWHEADER_HEIGHT = 50;

	public ImageDetailWindow(String description, File file) {
		super(description);

		// TODO kliknutím se otevře plná velikost v novém tabu
		try {
			BufferedImage bimg = null;
			bimg = ImageIO.read(file);

			if (bimg != null) {
				int width = bimg.getWidth();
				int height = bimg.getHeight();

				Image img = new Image(null, new FileResource(file));
				addComponent(img);
				((VerticalLayout) getContent()).setComponentAlignment(img,
						Alignment.MIDDLE_CENTER);
				setWidth((width + WINDOWPADDING) + "px");
				setHeight((height + WINDOWPADDING + WINDOWHEADER_HEIGHT) + "px");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		center();

	}

}

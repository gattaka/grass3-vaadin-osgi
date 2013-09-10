package org.myftp.gattserver.grass3.pg.pages;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.shared.AnimType;
import org.vaadin.peter.imagescaler.ImageScaler;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;

public class ImageDetailWindow extends GrassSubWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	private AnimatorProxy animatorProxy = new AnimatorProxy();
	private ImageScaler scaler = new ImageScaler();

	private int imageIndex = 0;

	public ImageDetailWindow(final File[] miniatures, final int index) {
		super(miniatures[index].getName());

		scaler.setWidth("1000px");
		scaler.setHeight("800px");
		addComponent(scaler);

		imageIndex = index;

		addComponent(animatorProxy);

		addShortcutListener(new ShortcutListener("previous",
				ShortcutAction.KeyCode.ARROW_LEFT, null) {
			private static final long serialVersionUID = -7462791636558905919L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (imageIndex > 0) {
					imageIndex--;
					showImage(miniatures, imageIndex);
				}
			}
		});

		addShortcutListener(new ShortcutListener("next",
				ShortcutAction.KeyCode.ARROW_RIGHT, null) {
			private static final long serialVersionUID = -7462791636558905919L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (imageIndex < miniatures.length - 1) {
					imageIndex++;
					showImage(miniatures, imageIndex);
				}
			}
		});

		showImage(miniatures, index);

		center();

	}

	protected void showImage(File[] miniatures, int index) {

		File image = new File(
				miniatures[index].getParentFile().getParentFile(),
				miniatures[index].getName());

		int width = 100;
		int height = 100;

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

		this.setCaption(image.getName() + ", " + width + "x" + height + " ("
				+ (index + 1) + "/" + miniatures.length + ")");

		scaler.setImage(new FileResource(image), width, height);
//		animatorProxy.animate(scaler, AnimType.FADE_IN).setDuration(500)
//				.setDelay(300);
	}

}

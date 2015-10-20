package cz.gattserver.grass3.pg.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.tepi.imageviewer.ImageViewer;
import org.vaadin.tepi.imageviewer.ImageViewer.ImageSelectedEvent;
import org.vaadin.tepi.imageviewer.ImageViewer.ImageSelectionListener;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.web.common.window.WebWindow;

public class ImageDetailWindow extends WebWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	public ImageDetailWindow(final File[] miniatures, final int index, File slideshowDir) {
		super(miniatures[index].getName());

		List<Resource> list = new ArrayList<>();

		for (File mini : miniatures) {

			File image = new File(slideshowDir, mini.getName());
			if (image.exists() == false)
				image = new File(slideshowDir.getParent(), mini.getName());

			Resource resource = new FileResource(image);
			list.add(resource);
		}

		ImageViewer imageViewer = new ImageViewer();
		imageViewer.setImages(list);
		imageViewer.setWidth("1460px");
		imageViewer.setHeight("710px");
		imageViewer.setImmediate(true);
		imageViewer.setCenterImageIndex(index - 1);
		// imageViewer.setCenterImageRelativeWidth(0.99f);
		imageViewer.setCenterImageRelativeWidth(0.7f);
		imageViewer.setSideImageRelativeWidth(0.5f);
		imageViewer.setSideImageCount(3);
		imageViewer.setAnimationDuration(300);
		imageViewer.focus();
		addComponent(imageViewer);
		((VerticalLayout) getContent()).setComponentAlignment(imageViewer, Alignment.MIDDLE_CENTER);

		imageViewer.addListener(new ImageSelectionListener() {

			@Override
			public void imageSelected(ImageSelectedEvent e) {
				int length = miniatures.length;
				int selectedIndex = (e.getSelectedImageIndex() + 1) % length;
				ImageDetailWindow.this.setCaption(miniatures[selectedIndex].getName());
			}
		});

		setWidth("1500px");
		setHeight("800px");

		center();

	}
}

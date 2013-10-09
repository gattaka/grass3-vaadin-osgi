package org.myftp.gattserver.grass3.pg.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.vaadin.tepi.imageviewer.ImageViewer;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

public class ImageDetailWindow extends GrassSubWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	public ImageDetailWindow(final File[] miniatures, final int index) {
		super(miniatures[index].getName());

		setWidth("1500px");
		setHeight("800px");

		List<Resource> list = new ArrayList<>();

		for (File mini : miniatures) {

			File image = new File(mini.getParentFile().getParentFile(),
					mini.getName());

			Resource resource = new FileResource(image);
			list.add(resource);
		}

		ImageViewer imageViewer = new ImageViewer(list);
		imageViewer.setWidth("1460px");
		imageViewer.setHeight("710px");
		imageViewer.setImmediate(true);
		imageViewer.setCenterImageIndex(index-1);
//		imageViewer.setCenterImageRelativeWidth(0.99f);
		imageViewer.setCenterImageRelativeWidth(0.7f);
		imageViewer.setSideImageRelativeWidth(0.5f);
		imageViewer.setSideImageCount(3);
		imageViewer.setAnimationDuration(300);
		imageViewer.focus();
		addComponent(imageViewer);
		((VerticalLayout) getContent()).setComponentAlignment(imageViewer,
				Alignment.MIDDLE_CENTER);

		center();

	}

}

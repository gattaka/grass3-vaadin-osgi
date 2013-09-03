package org.myftp.gattserver.grass3.pg.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("photogalleryViewerPageFactory")
public class PhotogalleryViewerPageFactory extends AbstractPageFactory {

	public PhotogalleryViewerPageFactory() {
		super("photogallery", "photogalleryViewerPage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}

package cz.gattserver.grass3.pg.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.ui.pages.PhotogalleryViewerPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("photogalleryViewerPageFactory")
public class PhotogalleryViewerPageFactory extends AbstractPageFactory {

	public PhotogalleryViewerPageFactory() {
		super("photogallery");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new PhotogalleryViewerPage(request);
	}
}

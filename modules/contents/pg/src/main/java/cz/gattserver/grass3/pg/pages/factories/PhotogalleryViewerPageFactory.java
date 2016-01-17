package cz.gattserver.grass3.pg.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.pg.pages.PhotogalleryViewerPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("photogalleryViewerPageFactory")
public class PhotogalleryViewerPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 5981090011575615856L;

	public PhotogalleryViewerPageFactory() {
		super("photogallery");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new PhotogalleryViewerPage(request);
	}
}
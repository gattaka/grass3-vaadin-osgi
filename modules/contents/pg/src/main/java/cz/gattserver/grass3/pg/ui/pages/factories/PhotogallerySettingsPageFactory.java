package cz.gattserver.grass3.pg.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.ui.pages.PhotogallerySettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component
public class PhotogallerySettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public PhotogallerySettingsPageFactory() {
		super("Fotogalerie", "photogallery");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new PhotogallerySettingsPage(request);
	}
}

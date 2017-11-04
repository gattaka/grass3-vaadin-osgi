package cz.gattserver.grass3.pg.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.pg.tabs.PhotogallerySettingsPage;
import cz.gattserver.grass3.tabs.factories.template.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.util.GrassRequest;

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

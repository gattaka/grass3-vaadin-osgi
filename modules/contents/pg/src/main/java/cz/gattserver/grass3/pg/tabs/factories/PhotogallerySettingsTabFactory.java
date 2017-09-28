package cz.gattserver.grass3.pg.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.tabs.PhotogallerySettingsTab;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("photogallerySettingsTabFactory")
public class PhotogallerySettingsTabFactory extends AbstractSettingsTabFactory {

	public PhotogallerySettingsTabFactory() {
		super("Fotogalerie", "photogallery");
	}

	public boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected SettingsTab createTab(GrassRequest request) {
		return new PhotogallerySettingsTab(request);
	}
}

package org.myftp.gattserver.grass3.pg.tabs.factories;

import org.myftp.gattserver.grass3.pg.tabs.PhotogallerySettingsTab;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.myftp.gattserver.grass3.tabs.template.ISettingsTab;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("photogallerySettingsTabFactory")
public class PhotogallerySettingsTabFactory extends AbstractSettingsTabFactory {

	public PhotogallerySettingsTabFactory() {
		super("Fotogalerie", "photogallery");
	}

	public boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new PhotogallerySettingsTab(request);
	}
}

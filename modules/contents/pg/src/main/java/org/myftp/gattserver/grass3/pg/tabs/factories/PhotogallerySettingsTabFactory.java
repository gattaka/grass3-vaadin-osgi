package org.myftp.gattserver.grass3.pg.tabs.factories;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("photogallerySettingsTabFactory")
public class PhotogallerySettingsTabFactory extends AbstractSettingsTabFactory {

	public PhotogallerySettingsTabFactory() {
		super("Fotogalerie", "photogallery", "photogallerySettingsTab");
	}

	public boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN);
	}
}

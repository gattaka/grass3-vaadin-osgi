package org.myftp.gattserver.grass3.articles.favlink.web;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("favlinkSettingsTabFactory")
public class FavlinkSettingsTabFactory extends AbstractSettingsTabFactory {

	public FavlinkSettingsTabFactory() {
		super("Favlink", "favlink", "favlinkSettingsTab");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
	}
}

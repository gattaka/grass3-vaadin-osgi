package cz.gattserver.grass3.articles.favlink.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.ISettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("favlinkSettingsTabFactory")
public class FavlinkSettingsTabFactory extends AbstractSettingsTabFactory {

	public FavlinkSettingsTabFactory() {
		super("Favlink", "favlink");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new FavlinkSettingsTab(request);
	}
}
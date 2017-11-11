package cz.gattserver.grass3.articles.favlink.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

@Component
public class FavlinkSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public FavlinkSettingsPageFactory() {
		super("Favlink", "favlink");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new FavlinkSettingsPage(request);
	}
}

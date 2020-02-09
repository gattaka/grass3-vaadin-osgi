package cz.gattserver.grass3.articles.plugins.favlink.ui.pages.settings.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.plugins.favlink.ui.pages.settings.FavlinkSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

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
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new FavlinkSettingsPageFragmentFactory();
	}
}

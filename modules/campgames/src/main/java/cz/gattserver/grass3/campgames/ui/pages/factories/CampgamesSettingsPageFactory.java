package cz.gattserver.grass3.campgames.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.campgames.ui.pages.CampgamesSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

@Component
public class CampgamesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public CampgamesSettingsPageFactory() {
		super("Evidence táborových her", "campgames");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new CampgamesSettingsPageFragmentFactory();
	}
}

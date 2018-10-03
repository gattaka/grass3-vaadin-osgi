package cz.gattserver.grass3.campgames.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.campgames.ui.pages.CampgamesSettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

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
	protected GrassPage createPage(GrassRequest request) {
		return new CampgamesSettingsPage(request);
	}
}

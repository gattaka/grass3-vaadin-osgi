package cz.gattserver.grass3.fm.web.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.fm.web.FMSettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component
public class FMSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public FMSettingsPageFactory() {
		super("Soubory", "fm");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new FMSettingsPage(request);
	}
}

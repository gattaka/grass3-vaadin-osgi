package cz.gattserver.grass3.hw.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.hw.ui.pages.HWSettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component
public class HWSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public HWSettingsPageFactory() {
		super("Evidence HW", "hw");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new HWSettingsPage(request);
	}
}

package cz.gattserver.grass3.hw.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.hw.ui.pages.HWSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

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
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new HWSettingsPageFragmentFactory();
	}
}

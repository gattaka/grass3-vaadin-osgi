package cz.gattserver.grass3.print3d.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.print3d.ui.pages.Print3dSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

@Component
public class Print3dSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public Print3dSettingsPageFactory() {
		super("Print3d", "print3d");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new Print3dSettingsPageFragmentFactory();
	}
}

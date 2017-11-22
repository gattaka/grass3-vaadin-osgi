package cz.gattserver.grass3.modules.register;

import cz.gattserver.grass3.ui.pages.settings.factories.ModuleSettingsPageFactory;

public interface ModuleSettingsPageFactoriesRegister {

	public ModuleSettingsPageFactory getFactory(String name);

	/**
	 * Tvorba aliasů
	 */
	public ModuleSettingsPageFactory putAlias(String settingsName, ModuleSettingsPageFactory factory);

}

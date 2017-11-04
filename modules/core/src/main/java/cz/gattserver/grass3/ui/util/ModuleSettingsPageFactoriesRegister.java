package cz.gattserver.grass3.ui.util;

import cz.gattserver.grass3.tabs.factories.template.ModuleSettingsPageFactory;

public interface ModuleSettingsPageFactoriesRegister {

	public ModuleSettingsPageFactory getFactory(String name);

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public ModuleSettingsPageFactory putAlias(String settingsName, ModuleSettingsPageFactory factory);

}

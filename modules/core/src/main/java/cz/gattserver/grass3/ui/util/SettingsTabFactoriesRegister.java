package cz.gattserver.grass3.ui.util;

import cz.gattserver.grass3.tabs.factories.template.SettingsTabFactory;

public interface SettingsTabFactoriesRegister {

	public SettingsTabFactory getFactory(String name);

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public SettingsTabFactory putAlias(String settingsTabName, SettingsTabFactory factory);

}

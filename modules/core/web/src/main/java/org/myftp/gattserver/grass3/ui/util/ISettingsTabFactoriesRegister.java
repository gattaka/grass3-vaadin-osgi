package org.myftp.gattserver.grass3.ui.util;

import org.myftp.gattserver.grass3.tabs.factories.template.ISettingsTabFactory;

public interface ISettingsTabFactoriesRegister {

	public ISettingsTabFactory getFactory(String name);

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public ISettingsTabFactory putAlias(String settingsTabName,
			ISettingsTabFactory factory);

}

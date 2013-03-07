package org.myftp.gattserver.grass3.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.myftp.gattserver.grass3.tabs.factories.template.SettingsTabFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "settingsTabFactoriesRegister")
public class SettingsTabFactoriesRegister {

	/**
	 * Hlavní mapa stránek
	 */
	private Map<String, SettingsTabFactory> factories = new HashMap<String, SettingsTabFactory>();

	@Autowired
	public SettingsTabFactoriesRegister(
			List<SettingsTabFactory> settingsTabFactories) {
		for (SettingsTabFactory factory : settingsTabFactories)
			factories.put(factory.getSettingsURL(), factory);
	}

	public SettingsTabFactory getFactory(String name) {
		return factories.get(name);
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public SettingsTabFactory putAlias(String settingsTabName,
			SettingsTabFactory factory) {
		return factories.put(settingsTabName, factory);
	}

}

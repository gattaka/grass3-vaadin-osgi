package cz.gattserver.grass3.ui.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.tabs.factories.template.SettingsTabFactory;
import cz.gattserver.grass3.ui.util.SettingsTabFactoriesRegister;

@Component(value = "settingsTabFactoriesRegister")
public class SettingsTabFactoriesRegisterImpl implements
		SettingsTabFactoriesRegister {

	@Autowired
	private List<SettingsTabFactory> settingsTabFactories;

	/**
	 * Hlavní mapa stránek
	 */
	private Map<String, SettingsTabFactory> factories = new HashMap<String, SettingsTabFactory>();

	@PostConstruct
	private void init() {
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

package cz.gattserver.grass3.ui.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.tabs.factories.template.ModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.util.ModuleSettingsPageFactoriesRegister;

@Component(value = "settingsTabFactoriesRegister")
public class SettingsTabFactoriesRegisterImpl implements
		ModuleSettingsPageFactoriesRegister {

	@Autowired
	private List<ModuleSettingsPageFactory> settingsTabFactories;

	/**
	 * Hlavní mapa stránek
	 */
	private Map<String, ModuleSettingsPageFactory> factories = new HashMap<String, ModuleSettingsPageFactory>();

	@PostConstruct
	private void init() {
		for (ModuleSettingsPageFactory factory : settingsTabFactories)
			factories.put(factory.getSettingsURL(), factory);
	}

	public ModuleSettingsPageFactory getFactory(String name) {
		return factories.get(name);
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public ModuleSettingsPageFactory putAlias(String settingsTabName,
			ModuleSettingsPageFactory factory) {
		return factories.put(settingsTabName, factory);
	}

}

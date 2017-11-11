package cz.gattserver.grass3.register.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.settings.factories.ModuleSettingsPageFactory;
import cz.gattserver.grass3.register.ModuleSettingsPageFactoriesRegister;

@Component(value = "settingsPageFactoriesRegister")
public class SettingsPageFactoriesRegisterImpl implements
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

	public ModuleSettingsPageFactory putAlias(String settingsTabName,
			ModuleSettingsPageFactory factory) {
		return factories.put(settingsTabName, factory);
	}

}

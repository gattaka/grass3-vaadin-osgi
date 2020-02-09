package cz.gattserver.grass3.modules.register.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.register.ModuleSettingsPageFactoriesRegister;
import cz.gattserver.grass3.ui.pages.settings.ModuleSettingsPageFactory;

@Component(value = "settingsPageFactoriesRegister")
public class ModuleSettingsPageFactoriesRegisterImpl implements ModuleSettingsPageFactoriesRegister {

	@Autowired
	private List<ModuleSettingsPageFactory> settingsTabFactories;

	/**
	 * Hlavní mapa stránek
	 */
	private Map<String, ModuleSettingsPageFactory> factories = new HashMap<>();

	@PostConstruct
	private void init() {
		for (ModuleSettingsPageFactory factory : settingsTabFactories)
			factories.put(factory.getSettingsURL(), factory);
	}

	public ModuleSettingsPageFactory getFactory(String name) {
		return factories.get(name);
	}

	public ModuleSettingsPageFactory putAlias(String settingsTabName, ModuleSettingsPageFactory factory) {
		return factories.put(settingsTabName, factory);
	}

}

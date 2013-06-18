package org.myftp.gattserver.grass3.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.myftp.gattserver.grass3.tabs.factories.template.ISettingsTabFactory;
import org.myftp.gattserver.grass3.util.ISettingsTabFactoriesRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "settingsTabFactoriesRegister")
public class SettingsTabFactoriesRegisterImpl implements
		ISettingsTabFactoriesRegister {

	@Autowired
	private List<ISettingsTabFactory> settingsTabFactories;

	/**
	 * Hlavní mapa stránek
	 */
	private Map<String, ISettingsTabFactory> factories = new HashMap<String, ISettingsTabFactory>();

	@PostConstruct
	private void init() {
		for (ISettingsTabFactory factory : settingsTabFactories)
			factories.put(factory.getSettingsURL(), factory);
	}

	public ISettingsTabFactory getFactory(String name) {
		return factories.get(name);
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public ISettingsTabFactory putAlias(String settingsTabName,
			ISettingsTabFactory factory) {
		return factories.put(settingsTabName, factory);
	}

}

package cz.gattserver.grass3.services;

import cz.gattserver.grass3.config.AbstractConfiguration;

public interface ConfigurationService {

	/**
	 * Nahraje existující konfiguraci a dopíše její vyplněné hodnoty do
	 * předaného objektu konfigurace
	 * 
	 * @param configuration
	 */
	public <T extends AbstractConfiguration> void loadConfiguration(T configuration);

	/**
	 * Uloží konfiguraci.
	 * 
	 * @param configuration
	 *            objekt konfigurace
	 */
	public <T extends AbstractConfiguration> void saveConfiguration(T configuration);
}
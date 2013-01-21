package org.myftp.gattserver.grass3.config;

import javax.xml.bind.JAXBException;

public class ConfigurationUtils<T> {

	private T defaultConfiguration;
	private String configPath;

	public ConfigurationUtils(T defaultConfiguration, String configPath) {
		this.defaultConfiguration = defaultConfiguration;
		this.configPath = configPath;
	}

	public T loadExistingOrCreateNewConfiguration() throws JAXBException {
		ConfigurationManager configurationManager = ConfigurationManager
				.getInstance();
		Object confObject = configurationManager.loadConfiguration(configPath,
				defaultConfiguration.getClass());
		T configuration = null;

		// existuje konfigurační soubor ?
		if (confObject == null) {
			// neexistuje - vytvoř implicitní a ulož
			configuration = defaultConfiguration;
			configurationManager.storeConfiguration(configPath, configuration);
		} else {
			configuration = (T) confObject;
		}
		return configuration;
	}

}

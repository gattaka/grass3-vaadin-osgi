package cz.gattserver.grass3.services;

import java.io.Serializable;

import cz.gattserver.grass3.config.AbstractConfiguration;

public interface ConfigurationService extends Serializable {

	public void loadConfiguration(AbstractConfiguration configuration);

	public boolean saveConfiguration(AbstractConfiguration configuration);
}

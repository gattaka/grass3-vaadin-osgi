package org.myftp.gattserver.grass3.config;

public interface IConfigurationService {

	public void loadConfiguration(AbstractConfiguration configuration);

	public boolean saveConfiguration(AbstractConfiguration configuration);
}

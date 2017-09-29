package cz.gattserver.grass3.config;

import java.io.Serializable;

public interface ConfigurationService extends Serializable {

	public void loadConfiguration(AbstractConfiguration configuration);

	public boolean saveConfiguration(AbstractConfiguration configuration);
}

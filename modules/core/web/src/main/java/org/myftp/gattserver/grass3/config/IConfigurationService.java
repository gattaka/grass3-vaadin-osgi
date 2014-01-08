package org.myftp.gattserver.grass3.config;

import java.io.Serializable;

public interface IConfigurationService extends Serializable {

	public void loadConfiguration(AbstractConfiguration configuration);

	public boolean saveConfiguration(AbstractConfiguration configuration);
}

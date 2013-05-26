package org.myftp.gattserver.grass3.model.dao;

import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ConfigurationItem;
import org.springframework.stereotype.Component;

@Component("configurationDAO")
public class ConfigurationDAO extends AbstractDAO<ConfigurationItem> {

	public ConfigurationDAO() {
		super(ConfigurationItem.class);
	}

}

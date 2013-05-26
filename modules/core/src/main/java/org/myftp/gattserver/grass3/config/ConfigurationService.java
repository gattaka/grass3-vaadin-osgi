package org.myftp.gattserver.grass3.config;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.dao.ConfigurationDAO;
import org.myftp.gattserver.grass3.model.domain.ConfigurationItem;
import org.springframework.stereotype.Component;

@Component("configurationService")
public class ConfigurationService implements IConfigurationService {

	@Resource(name = "configurationDAO")
	private ConfigurationDAO configurationDAO;

	public void loadConfiguration(
			AbstractConfiguration configuration) {

		List<ConfigurationItem> configurationItems = configurationDAO
				.findByRestriction(
						Restrictions.like("name", configuration.getPrefix()
								+ "%"), null, null);

		configuration.populateConfigurationFromMap(configurationItems);
	}

	public boolean saveConfiguration(AbstractConfiguration configuration) {
		List<ConfigurationItem> items = configuration.getConfigurationItems();
		for (ConfigurationItem item : items) {
			ConfigurationItem loadedItem = configurationDAO.findByID(item
					.getName());
			if (loadedItem == null) {
				configurationDAO.save(item);
			} else {
				loadedItem.setValue(item.getValue());
				configurationDAO.merge(loadedItem);
			}
		}

		return true;
	}
}

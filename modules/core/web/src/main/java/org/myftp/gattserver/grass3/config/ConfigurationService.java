package org.myftp.gattserver.grass3.config;

import java.util.List;

import org.myftp.gattserver.grass3.model.dao.ConfigurationItemRepository;
import org.myftp.gattserver.grass3.model.domain.ConfigurationItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("configurationService")
public class ConfigurationService implements IConfigurationService {

	private static final long serialVersionUID = -2565316748839842203L;

	@Autowired
	private ConfigurationItemRepository configurationItemRepository;

	public void loadConfiguration(AbstractConfiguration configuration) {

		List<ConfigurationItem> configurationItems = configurationItemRepository.findByNameStartingWith(configuration
				.getPrefix());

		configuration.populateConfigurationFromMap(configurationItems);
	}

	public boolean saveConfiguration(AbstractConfiguration configuration) {
		List<ConfigurationItem> items = configuration.getConfigurationItems();
		for (ConfigurationItem item : items) {
			ConfigurationItem loadedItem = configurationItemRepository.findOne(item.getName());
			if (loadedItem == null) {
				configurationItemRepository.save(item);
			} else {
				loadedItem.setValue(item.getValue());
				configurationItemRepository.save(loadedItem);
			}
		}

		return true;
	}
}

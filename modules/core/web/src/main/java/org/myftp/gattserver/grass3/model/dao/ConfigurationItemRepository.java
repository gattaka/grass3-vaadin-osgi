package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.myftp.gattserver.grass3.model.domain.ConfigurationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationItemRepository extends
		JpaRepository<ConfigurationItem, String> {

	public List<ConfigurationItem> findByNameStartingWith(String prefix);

}

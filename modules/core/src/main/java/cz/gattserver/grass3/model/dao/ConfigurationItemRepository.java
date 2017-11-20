package cz.gattserver.grass3.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.model.domain.ConfigurationItem;

public interface ConfigurationItemRepository extends JpaRepository<ConfigurationItem, String> {

	List<ConfigurationItem> findByNameStartingWith(String prefix);

}

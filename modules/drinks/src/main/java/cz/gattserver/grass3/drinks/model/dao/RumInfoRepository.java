package cz.gattserver.grass3.drinks.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.drinks.model.domain.RumInfo;

public interface RumInfoRepository extends JpaRepository<RumInfo, Long> {

}

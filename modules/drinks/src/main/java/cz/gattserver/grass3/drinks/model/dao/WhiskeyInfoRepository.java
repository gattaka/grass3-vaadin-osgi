package cz.gattserver.grass3.drinks.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.drinks.model.domain.WhiskeyInfo;

public interface WhiskeyInfoRepository extends JpaRepository<WhiskeyInfo, Long> {

}

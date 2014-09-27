package cz.gattserver.grass3.hw.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.hw.domain.HWItemType;

public interface HWItemTypeRepository extends JpaRepository<HWItemType, Long> {

}

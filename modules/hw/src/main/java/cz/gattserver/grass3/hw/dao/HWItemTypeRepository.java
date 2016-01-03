package cz.gattserver.grass3.hw.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.hw.domain.HWItemType;

public interface HWItemTypeRepository extends JpaRepository<HWItemType, Long> {

	@Query("select t from HWItemType t order by name asc")
	List<HWItemType> findListOrderByName();

}

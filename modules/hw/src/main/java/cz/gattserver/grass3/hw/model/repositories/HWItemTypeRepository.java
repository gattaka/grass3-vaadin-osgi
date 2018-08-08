package cz.gattserver.grass3.hw.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.hw.model.domain.HWItemType;

public interface HWItemTypeRepository extends JpaRepository<HWItemType, Long> {

	@Query("select t from HW_ITEM_TYPE t order by name asc")
	List<HWItemType> findListOrderByName();

	@Query("select t from HW_ITEM_TYPE t where t.name = ?1")
	HWItemType findByName(String name);

}

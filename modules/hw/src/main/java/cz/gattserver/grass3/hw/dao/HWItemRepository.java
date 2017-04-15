package cz.gattserver.grass3.hw.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.hw.domain.HWItem;

public interface HWItemRepository extends JpaRepository<HWItem, Long>, HWItemRepositoryCustom {

	public List<HWItem> findByTypesId(Long id);

	public List<HWItem> findByUsedInId(Long id);

	@Query("select i from HWItem i inner join i.types types where types.name in ?1")
	public List<HWItem> getHWItemsByTypes(Collection<String> types);
}

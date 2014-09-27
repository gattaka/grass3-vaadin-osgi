package cz.gattserver.grass3.hw.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.hw.domain.HWItem;

public interface HWItemRepository extends JpaRepository<HWItem, Long> {

	public List<HWItem> findByTypesId(Long id);

	public List<HWItem> findByUsedInId(Long id);
}

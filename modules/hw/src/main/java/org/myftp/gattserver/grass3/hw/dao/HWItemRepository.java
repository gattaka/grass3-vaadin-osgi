package org.myftp.gattserver.grass3.hw.dao;

import java.util.List;

import org.myftp.gattserver.grass3.hw.domain.HWItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HWItemRepository extends JpaRepository<HWItem, Long> {

	public List<HWItem> findByTypesId(Long id);
}

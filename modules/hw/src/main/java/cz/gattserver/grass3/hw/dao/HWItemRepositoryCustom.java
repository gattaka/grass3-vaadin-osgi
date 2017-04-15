package cz.gattserver.grass3.hw.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.domain.HWItem;
import cz.gattserver.grass3.hw.dto.HWFilterDTO;

public interface HWItemRepositoryCustom {

	long countHWItems(HWFilterDTO filter);

	List<HWItem> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order);
}

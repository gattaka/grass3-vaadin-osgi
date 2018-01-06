package cz.gattserver.grass3.hw.model.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.interfaces.HWFilterDTO;
import cz.gattserver.grass3.hw.model.domain.HWItem;

public interface HWItemRepositoryCustom {

	long countHWItems(HWFilterDTO filter);

	List<HWItem> getHWItems(HWFilterDTO filter, Pageable pageable, OrderSpecifier<?>[] order);
}

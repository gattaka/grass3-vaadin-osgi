package cz.gattserver.grass3.hw.model.repositories;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.model.domain.HWItemType;

public interface HWItemTypeRepositoryCustom {

	List<HWItemType> getHWItemTypes(HWItemTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	long countHWItemTypes(HWItemTypeTO filter);
}

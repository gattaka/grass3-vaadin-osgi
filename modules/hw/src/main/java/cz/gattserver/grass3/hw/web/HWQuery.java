package cz.gattserver.grass3.hw.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.hw.dto.HWFilterDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.web.common.SpringContextHelper;

public class HWQuery extends AbstractBeanQuery<HWItemOverviewDTO> {

	public static final String FILTER_KEY = "FILTER_KEY";
	
	public static final Integer PAGE_SIZE = 100;

	@Autowired
	protected HWFacade hwFacade;

	private HWFilterDTO filter;

	public HWQuery(QueryDefinition definition, Map<String, Object> queryConfiguration, Object[] sortPropertyIds,
			boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		SpringContextHelper.inject(this);
		filter = (HWFilterDTO) queryConfiguration.get(FILTER_KEY);
	}

	@Override
	protected HWItemOverviewDTO constructBean() {
		return new HWItemOverviewDTO();
	}

	@Override
	public int size() {
		return (int) hwFacade.countHWItems(filter);
	}

	@Override
	protected List<HWItemOverviewDTO> loadBeans(int startIndex, int count) {
		OrderSpecifier<String>[] specifiers = QuerydslUtil.transformOrdering(getSortPropertyIds(), getSortStates());
		return hwFacade.getHWItems(filter, new PageRequest(startIndex / PAGE_SIZE, PAGE_SIZE), specifiers);
	}

	@Override
	protected void saveBeans(List<HWItemOverviewDTO> addedBeans, List<HWItemOverviewDTO> modifiedBeans,
			List<HWItemOverviewDTO> removedBeans) {
		// not implemented
	}
	
}

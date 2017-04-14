package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class RecentAddedContentsQuery extends LazyContentsQuery {

	public RecentAddedContentsQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
	}

	@Override
	protected int getSize() {
		return contentNodeFacade.getContentsCount();
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getRecentAddedForOverview(page, count));
	}

}

package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;

public class RecentModifiedContentsQuery extends LazyContentsQuery {

	public RecentModifiedContentsQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
	}

	@Override
	protected int getSize() {
		return contentNodeFacade.getContentsCount();
	}

	@Override
	protected List<ContentNodeDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getRecentModifiedForOverview(page, count));
	}

}

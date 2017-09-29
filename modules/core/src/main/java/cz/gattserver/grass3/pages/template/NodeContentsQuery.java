package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class NodeContentsQuery extends LazyContentsQuery {

	public final static String KEY = "NODE_ID";

	private Long nodeId;

	public NodeContentsQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		this.nodeId = (Long) queryConfiguration.get(KEY);
	}

	@Override
	protected int getSize() {
		return contentNodeFacade.getCountByNode(nodeId);
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getByNode(nodeId, page, count));
	}

}

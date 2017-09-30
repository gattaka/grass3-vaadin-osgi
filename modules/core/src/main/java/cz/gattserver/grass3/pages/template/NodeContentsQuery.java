package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class NodeContentsQuery extends LazyContentsQuery {

	public final static String KEY = "NODE_ID";

	private Long nodeId;

	@Override
	protected int getSize() {
		return contentNodeFacade.getCountByNode(nodeId);
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getByNode(nodeId, page, count));
	}

}

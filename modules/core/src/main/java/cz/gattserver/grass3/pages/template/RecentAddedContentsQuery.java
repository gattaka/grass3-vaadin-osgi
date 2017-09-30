package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class RecentAddedContentsQuery extends LazyContentsQuery {

	@Override
	protected int getSize() {
		return contentNodeFacade.getCount();
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getRecentAdded(page, count));
	}

}

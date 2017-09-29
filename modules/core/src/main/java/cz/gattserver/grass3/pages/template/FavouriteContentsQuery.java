package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class FavouriteContentsQuery extends LazyContentsQuery {

	public final static String KEY = "USER_ID";

	private Long userId;

	public FavouriteContentsQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		this.userId = (Long) queryConfiguration.get(KEY);
	}

	@Override
	protected int getSize() {
		return contentNodeFacade.getUserFavouriteCount(userId);
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getUserFavourite(userId, page, count));
	}

}

package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class FavouriteContentsQuery extends LazyContentsQuery {

	public final static String KEY = "USER_ID";

	private Long userId;

	@Override
	protected int getSize() {
		return contentNodeFacade.getUserFavouriteCount(userId);
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getUserFavourite(userId, page, count));
	}

}

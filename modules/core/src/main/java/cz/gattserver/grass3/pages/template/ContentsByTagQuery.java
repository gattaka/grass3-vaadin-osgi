package cz.gattserver.grass3.pages.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public class ContentsByTagQuery extends LazyContentsQuery {

	public final static String KEY = "TAG_ID";

	private Long tagId;

	public ContentsByTagQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		this.tagId = (Long) queryConfiguration.get(KEY);
	}

	@Override
	protected int getSize() {
		return contentNodeFacade.getCountByTag(tagId);
	}

	@Override
	protected List<ContentNodeOverviewDTO> getBeans(int page, int count) {
		return new ArrayList<>(contentNodeFacade.getByTag(tagId, page, count));
	}

}

package cz.gattserver.grass3.pages.template;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.web.common.SpringContextHelper;

public abstract class LazyContentsQuery extends AbstractBeanQuery<ContentNodeOverviewDTO> {

	@Autowired
	protected ContentNodeFacade contentNodeFacade;

	public LazyContentsQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		SpringContextHelper.inject(this);
	}

	@Override
	protected ContentNodeOverviewDTO constructBean() {
		return new ContentNodeOverviewDTO();
	}

	@Override
	public int size() {
		return getSize();
	}

	@Override
	protected List<ContentNodeOverviewDTO> loadBeans(int startIndex, int count) {
		return getBeans(startIndex / count, count);
	}

	@Override
	protected void saveBeans(List<ContentNodeOverviewDTO> addedBeans, List<ContentNodeOverviewDTO> modifiedBeans,
			List<ContentNodeOverviewDTO> removedBeans) {
		// not implemented
	}

	protected abstract int getSize();

	protected abstract List<ContentNodeOverviewDTO> getBeans(int page, int count);

}

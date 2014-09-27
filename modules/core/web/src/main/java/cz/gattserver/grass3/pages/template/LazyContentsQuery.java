package cz.gattserver.grass3.pages.template;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;

public abstract class LazyContentsQuery extends AbstractBeanQuery<ContentNodeDTO> {

	@Autowired
	protected IContentNodeFacade contentNodeFacade;

	public LazyContentsQuery(QueryDefinition definition, Map<String, Object> queryConfiguration,
			Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		SpringContextHelper.inject(this);
	}

	@Override
	protected ContentNodeDTO constructBean() {
		return new ContentNodeDTO();
	}

	@Override
	public int size() {
		return getSize();
	}

	@Override
	protected List<ContentNodeDTO> loadBeans(int startIndex, int count) {
		return getBeans(startIndex / count, count);
	}

	@Override
	protected void saveBeans(List<ContentNodeDTO> addedBeans, List<ContentNodeDTO> modifiedBeans,
			List<ContentNodeDTO> removedBeans) {
		// not implemented
	}

	protected abstract int getSize();

	protected abstract List<ContentNodeDTO> getBeans(int page, int count);

}

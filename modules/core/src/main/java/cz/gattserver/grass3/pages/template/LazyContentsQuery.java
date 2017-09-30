package cz.gattserver.grass3.pages.template;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public abstract class LazyContentsQuery {

	@Autowired
	protected ContentNodeFacade contentNodeFacade;

	protected ContentNodeOverviewDTO constructBean() {
		return new ContentNodeOverviewDTO();
	}

	public int size() {
		return getSize();
	}

	protected List<ContentNodeOverviewDTO> loadBeans(int startIndex, int count) {
		return getBeans(startIndex / count, count);
	}

	protected void saveBeans(List<ContentNodeOverviewDTO> addedBeans, List<ContentNodeOverviewDTO> modifiedBeans,
			List<ContentNodeOverviewDTO> removedBeans) {
		// not implemented
	}

	protected abstract int getSize();

	protected abstract List<ContentNodeOverviewDTO> getBeans(int page, int count);

}

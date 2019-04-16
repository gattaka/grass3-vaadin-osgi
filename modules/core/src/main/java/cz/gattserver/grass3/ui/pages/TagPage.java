package cz.gattserver.grass3.ui.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.template.BasePage;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;

public class TagPage extends BasePage {

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	public TagPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {

		String tagName = getRequest().getAnalyzer().getNextPathToken();
		if (tagName == null)
			throw new GrassPageException(404);

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(tagName);
		if (identifier == null)
			throw new GrassPageException(404);

		ContentTagOverviewTO tag = contentTagFacade.getTagById(identifier.getId());

		if (tag == null)
			throw new GrassPageException(404);

		ContentsLazyGrid tagContentsTable = new ContentsLazyGrid();
		tagContentsTable.populate(this,
				(sortOrder, offset, limit) -> contentNodeFacade.getByTag(tag.getId(), offset, limit).stream(),
				() -> contentNodeFacade.getCountByTag(tag.getId()));

		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout pagelayout = new VerticalLayout();

		pagelayout.setMargin(true);
		pagelayout.setSpacing(true);

		// Obsahy
		VerticalLayout contentNodesLayout = new VerticalLayout();
		contentNodesLayout.setSpacing(true);
		contentNodesLayout.addComponent(new H2Label("Obsahy označené tagem: " + tag.getName()));

		contentNodesLayout.addComponent(tagContentsTable);
		tagContentsTable.setWidth("100%");
		tagContentsTable.setHeight("300px");

		pagelayout.addComponent(contentNodesLayout);

		contentLayout.addComponent(pagelayout, "content");
	}

}

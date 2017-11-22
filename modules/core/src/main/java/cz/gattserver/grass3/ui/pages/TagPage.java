package cz.gattserver.grass3.ui.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.template.BasePage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;

public class TagPage extends BasePage {

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	private ContentTagOverviewTO tag;

	public TagPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {

		String tagName = getRequest().getAnalyzer().getNextPathToken();
		if (tagName == null)
			UIUtils.showErrorPage404();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(tagName);
		if (identifier == null) {
			UIUtils.showErrorPage404();
			return;
		}

		tag = contentTagFacade.getTagById(identifier.getId());

		if (tag == null) {
			UIUtils.showErrorPage404();
			return;
		}

		ContentsLazyGrid tagContentsTable = new ContentsLazyGrid();
		tagContentsTable.populate(this, (sortOrder, offset, limit) -> {
			return contentNodeFacade.getByTag(tag.getId(), offset / limit, limit).stream();
		}, () -> {
			return contentNodeFacade.getCountByTag(tag.getId());
		});

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

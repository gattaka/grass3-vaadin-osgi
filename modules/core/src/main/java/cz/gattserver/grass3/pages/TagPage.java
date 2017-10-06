package cz.gattserver.grass3.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.pages.template.BasePage;
import cz.gattserver.grass3.pages.template.ContentsLazyGrid;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;

public class TagPage extends BasePage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	private ContentTagDTO tag;

	public TagPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {

		String tagName = getRequest().getAnalyzer().getNextPathToken();
		if (tagName == null)
			showError404();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(tagName);
		if (identifier == null) {
			showError404();
			return;
		}

		tag = contentTagFacade.getContentTagById(identifier.getId());

		if (tag == null) {
			showError404();
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

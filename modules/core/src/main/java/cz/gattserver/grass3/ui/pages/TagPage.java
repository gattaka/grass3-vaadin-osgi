package cz.gattserver.grass3.ui.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

public class TagPage extends OneColumnPage {

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	public TagPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createColumnContent() {
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
				q -> contentNodeFacade.getByTag(tag.getId(), q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCountByTag(tag.getId()));

		VerticalLayout pagelayout = new VerticalLayout();

		pagelayout.setPadding(true);
		pagelayout.setSpacing(true);

		// Obsahy
		VerticalLayout contentNodesLayout = new VerticalLayout();
		contentNodesLayout.setSpacing(true);
		contentNodesLayout.add(new H2("Obsahy označené tagem: " + tag.getName()));

		contentNodesLayout.add(tagContentsTable);
		tagContentsTable.setWidth("100%");
		tagContentsTable.setHeight("300px");

		pagelayout.add(contentNodesLayout);

		return pagelayout;
	}

}

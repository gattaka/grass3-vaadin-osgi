package cz.gattserver.grass3.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.pages.template.BasePage;
import cz.gattserver.grass3.pages.template.ContentsLazyTable;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;

public class TagPage extends BasePage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	private ContentTagDTO tag;
	private Label tagLabel;
	private String tagLabelPrefix;
	private String tagLabelSuffix;

	public TagPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {

		tagLabelPrefix = "<h2>Obsahy označené tagem: ";
		tagLabelSuffix = "</h2>";

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

		ContentsLazyTable tagContentsTable = new ContentsLazyTable();
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
		contentNodesLayout.addComponent(tagLabel = new Label());

		contentNodesLayout.addComponent(tagContentsTable);
		tagContentsTable.setWidth("100%");
		tagContentsTable.setHeight("300px");

		pagelayout.addComponent(contentNodesLayout);

		tagLabel.setValue(tagLabelPrefix + tag.getName() + tagLabelSuffix);
		tagLabel.setContentMode(ContentMode.HTML);

		contentLayout.addComponent(pagelayout, "content");
	}

}

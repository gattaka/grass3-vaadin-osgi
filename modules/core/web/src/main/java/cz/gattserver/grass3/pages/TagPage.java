package cz.gattserver.grass3.pages;

import javax.annotation.Resource;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.IContentTagFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.pages.template.BasePage;
import cz.gattserver.grass3.pages.template.ContentsTableFactory;
import cz.gattserver.grass3.pages.template.ContentsTableFactory.ContentsTable;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;

public class TagPage extends BasePage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "contentsTableFactory")
	private ContentsTableFactory contentsTableFactory;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

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
		
		ContentsTable tagContentsTable = contentsTableFactory.createContentsTable();

		String tagName = getRequest().getAnalyzer().getPathToken(1);
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

		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout pagelayout = new VerticalLayout();

		pagelayout.setMargin(true);
		pagelayout.setSpacing(true);

		// Obsahy
		VerticalLayout contentNodesLayout = new VerticalLayout();
		contentNodesLayout.setSpacing(true);
		contentNodesLayout.addComponent(tagLabel = new Label());

		int nonPublicatedContents = 0;
		for (ContentNodeDTO content : tag.getContentNodes()) {
			if (coreACL.canShowContent(content, getUser()) == false)
				nonPublicatedContents++;
		}

		if (nonPublicatedContents > 0) {
			HorizontalLayout publicatedLayout = new HorizontalLayout();
			publicatedLayout.setSpacing(true);
			publicatedLayout.setMargin(false);
			publicatedLayout.addStyleName("not-publicated-info");
			publicatedLayout.addComponent(new Embedded(null, new ThemeResource("img/tags/info_16.png")));

			StringBuilder builder = new StringBuilder();
			builder.append("<strong>");
			builder.append(nonPublicatedContents);
			switch (nonPublicatedContents) {
			case 1:
				builder.append(" obsah nebyl zatím publikován");
				break;
			case 2:
			case 3:
			case 4:
				builder.append(" obsahy nebyly zatím publikovány");
				break;
			default:
				builder.append(" obsahů nebylo zatím publikováno");
				break;
			}
			builder.append("</strong>");
			publicatedLayout.addComponent(new Label(builder.toString(), ContentMode.HTML));
			contentNodesLayout.addComponent(publicatedLayout);
		}

		contentNodesLayout.addComponent(tagContentsTable);
		tagContentsTable.setWidth("100%");

		pagelayout.addComponent(contentNodesLayout);

		tagContentsTable.populateTable(tag.getContentNodes(), this);
		tagLabel.setValue(tagLabelPrefix + tag.getName() + tagLabelSuffix);
		tagLabel.setContentMode(ContentMode.HTML);

		contentLayout.addComponent(pagelayout, "content");
	}

}

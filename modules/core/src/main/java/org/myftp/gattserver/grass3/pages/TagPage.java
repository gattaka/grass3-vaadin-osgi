package org.myftp.gattserver.grass3.pages;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.pages.template.BasePage;
import org.myftp.gattserver.grass3.pages.template.ContentsTableFactory;
import org.myftp.gattserver.grass3.pages.template.ContentsTableFactory.ContentsTable;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@Component("tagPage")
@Scope("prototype")
public class TagPage extends BasePage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "contentsTableFactory")
	private ContentsTableFactory contentsTableFactory;

	private ContentTagDTO tag;
	private Label tagLabel;
	private String tagLabelPrefix = "<h2>Obsahy označené tagem: ";
	private String tagLabelSuffix = "</h2>";

	public TagPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {

		ContentsTable tagContentsTable = contentsTableFactory
				.createContentsTable();

		tag = contentTagFacade.getContentTagByName(getRequest().getAnalyzer()
				.getPathToken(1));

		if (tag == null) {
			showError404();
			return;
		}

		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");

		VerticalLayout pagelayout = new VerticalLayout();

		pagelayout.setMargin(true);
		pagelayout.setSpacing(true);

		// Nedávno upravené obsahy
		VerticalLayout contentNodesLayout = new VerticalLayout();
		contentNodesLayout.addComponent(tagLabel = new Label());
		contentNodesLayout.addComponent(tagContentsTable);
		tagContentsTable.setWidth("100%");
		pagelayout.addComponent(contentNodesLayout);

		tagContentsTable.populateTable(tag.getContentNodes(), this);
		tagLabel.setValue(tagLabelPrefix + tag.getName() + tagLabelSuffix);
		tagLabel.setContentMode(ContentMode.HTML);

		contentLayout.addComponent(pagelayout, "content");
	}

}

package org.myftp.gattserver.grass3.windows;

import java.net.URL;

import org.myftp.gattserver.grass3.facades.ContentTagFacade;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.windows.template.ContentsTable;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class TagWindow extends OneColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;

	public static final String NAME = "tag";

	private final ContentsTable tagContentsTable = new ContentsTable();

	private ContentTagDTO tag;
	private Label tagLabel;
	private String tagLabelPrefix = "<h2>Obsahy označené tagem: ";
	private String tagLabelSuffix = "</h2>";

	public TagWindow() {
		setName(NAME);
		setCaption("Gattserver");
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);

		// Nedávno upravené obsahy
		VerticalLayout contentNodesLayout = new VerticalLayout();
		contentNodesLayout.addComponent(tagLabel = new Label());
		contentNodesLayout.addComponent(tagContentsTable);
		tagContentsTable.setWidth("100%");
		layout.addComponent(contentNodesLayout);

	}

	@Override
	protected void onShow() {

		tagContentsTable.populateTable(tag.getContentNodes(), this);
		tagLabel.setValue(tagLabelPrefix + tag.getName() + tagLabelSuffix);
		tagLabel.setContentMode(Label.CONTENT_XHTML);

		super.onShow();
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		if (relativeUri.length() == 0)
			showError404();

		tag = contentTagFacade.getContentTagByName(relativeUri);

		if (tag == null)
			showError404();

		return super.handleURI(context, relativeUri);
	}

}

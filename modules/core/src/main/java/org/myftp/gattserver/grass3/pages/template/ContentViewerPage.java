package org.myftp.gattserver.grass3.pages.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public abstract class ContentViewerPage extends TwoColumnPage {

	private static final long serialVersionUID = 5078280973817331002L;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "tagPageFactory")
	private IPageFactory tagPageFactory;

	private ContentNodeDTO content;
	private Label contentNameLabel;
	private Label contentAuthorNameLabel;
	private Label contentCreationDateNameLabel;
	private Label contentLastModificationDateLabel;
	private CssLayout tagsListLayout = new CssLayout();
	private CssLayout operationsListLayout;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb = new Breadcrumb();

	public ContentViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {

		content = getContentNodeDTO();
		updateBreadcrumb(content);

		SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");

		contentNameLabel = new Label("<h2>" + content.getName() + "</h2>");
		contentNameLabel.setContentMode(ContentMode.HTML);

		contentAuthorNameLabel = new Label(content.getAuthor().getName());

		contentCreationDateNameLabel = new Label(dateFormat.format(content
				.getCreationDate()));

		contentLastModificationDateLabel = new Label(
				content.getLastModificationDate() == null ? "<em>-neupraveno-</em>"
						: dateFormat.format(content.getLastModificationDate()));
		contentLastModificationDateLabel.setContentMode(ContentMode.HTML);

		tagsListLayout.removeAllComponents();
		for (String contentTag : content.getContentTags()) {
			Link tagLink = new Link(contentTag, getPageResource(tagPageFactory,
					contentTag));
			tagLink.addStyleName("taglabel");
			tagsListLayout.addComponent(tagLink);
		}

		operationsListLayout = new CssLayout();
		updateOperationsList(operationsListLayout);

		super.init();
	}

	@Override
	protected Component createLeftColumnContent() {

		CustomLayout layout = new CustomLayout("contentView");

		// info - přehled
		layout.addComponent(contentAuthorNameLabel, "author");
		layout.addComponent(contentCreationDateNameLabel, "createDate");
		layout.addComponent(contentLastModificationDateLabel, "modifyDate");

		// tagy
		layout.addComponent(tagsListLayout, "tags");

		// nástrojová lišta
		layout.addComponent(operationsListLayout, "operations");

		return layout;

	}

	@Override
	protected Component createRightColumnContent() {

		// TODO obsahy článků potřebují tady mít právě CustomLayout aby se dalo
		// pomocí JS roztahovat - jinak je to závislé na předpočítané pevné
		// výšce layoutu, který si vaadin počítá v době renderu a pak nemění !
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		layout.addComponent(breadcrumb);

		// Název obsahu
		VerticalLayout nameLayout = new VerticalLayout();
		layout.addComponent(nameLayout);
		nameLayout.addComponent(contentNameLabel);

		// samotný obsah
		createContent(layout);

		return layout;

	}

	protected abstract void createContent(VerticalLayout layout);

	protected abstract ContentNodeDTO getContentNodeDTO();

	protected abstract IPageFactory getContentViewerPageFactory();

	private void updateBreadcrumb(ContentNodeDTO content) {

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();

		/**
		 * obsah
		 */
		breadcrumbElements.add(new BreadcrumbElement(content.getName(),
				getPageResource(
						getContentViewerPageFactory(),
						URLIdentifierUtils.createURLIdentifier(
								content.getContentID(), content.getName()))));

		/**
		 * kategorie
		 */
		NodeDTO parent = content.getParent();
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showError404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(),
					getPageResource(
							categoryPageFactory,
							URLIdentifierUtils.createURLIdentifier(
									parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParentID() == null)
				break;

			parent = nodeFacade.getNodeById(parent.getParentID());
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	protected abstract void updateOperationsList(CssLayout operationsListLayout);
}

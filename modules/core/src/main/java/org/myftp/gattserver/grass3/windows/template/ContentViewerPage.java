package org.myftp.gattserver.grass3.windows.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.CategoryPage;
import org.myftp.gattserver.grass3.windows.TagPage;
import org.myftp.gattserver.grass3.windows.template.TwoColumnPage;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public abstract class ContentViewerPage extends TwoColumnPage {

	private static final long serialVersionUID = 5078280973817331002L;

	private ContentNodeDTO content;
	private Label contentNameLabel;
	private Label contentAuthorNameLabel;
	private Label contentCreationDateNameLabel;
	private Label contentLastModificationDateLabel;
	private CssLayout tagsListLayout = new CssLayout();
	private CssLayout operationsListLayout;

	private PageFactory pageFactory;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	public ContentViewerPage(GrassRequest request, PageFactory pageFactory) {
		super(request);
		this.pageFactory = pageFactory;

		content = getContentNodeDTO();
		updateBreadcrumb(content);

		SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");

		contentNameLabel.setValue("<h2>" + content.getName() + "</h2>");
		contentAuthorNameLabel.setValue(content.getAuthor().getName());
		contentCreationDateNameLabel.setValue(dateFormat.format(content
				.getCreationDate()));
		contentLastModificationDateLabel.setValue(content
				.getLastModificationDate() == null ? "<em>-neupraveno-</em>"
				: dateFormat.format(content.getLastModificationDate()));

		tagsListLayout.removeAllComponents();
		for (String contentTag : content.getContentTags()) {
			Link tagLink = new Link(contentTag, getPageResource(
					TagPage.FACTORY, contentTag));
			tagLink.addStyleName("taglabel");
			tagsListLayout.addComponent(tagLink);
		}

		operationsListLayout.removeAllComponents();
		updateOperationsList(operationsListLayout);
	}

	@Override
	protected Component createLeftColumnContent() {

		// TODO tohle by šlo mnohem elegantněji přepsat do customLayoutu
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);

		// info - přehled
		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setMargin(new MarginInfo(false, false, true, false));
		layout.addComponent(infoLayout);
		infoLayout.addComponent(new Label("<h2>Info</h2>", ContentMode.HTML));

		GridLayout gridLayout = new GridLayout(2, 3);
		infoLayout.addComponent(gridLayout);

		gridLayout.setSpacing(true);

		gridLayout.addComponent(new Label("<strong>Autor:</strong>",
				ContentMode.HTML), 0, 0);
		gridLayout.addComponent(contentAuthorNameLabel = new Label(), 1, 0);
		gridLayout.addComponent(new Label("<strong>Vytvořeno:</strong>",
				ContentMode.HTML), 0, 1);
		gridLayout.addComponent(contentCreationDateNameLabel = new Label(), 1,
				1);
		gridLayout.addComponent(new Label("<strong>Upraveno:</strong>",
				ContentMode.HTML), 0, 2);
		gridLayout.addComponent(contentLastModificationDateLabel = new Label(),
				1, 2);

		gridLayout.setComponentAlignment(contentAuthorNameLabel,
				Alignment.TOP_RIGHT);
		gridLayout.setComponentAlignment(contentCreationDateNameLabel,
				Alignment.TOP_RIGHT);
		gridLayout.setComponentAlignment(contentLastModificationDateLabel,
				Alignment.TOP_RIGHT);

		contentLastModificationDateLabel.setContentMode(ContentMode.HTML);

		contentAuthorNameLabel.setSizeUndefined();
		contentCreationDateNameLabel.setSizeUndefined();
		contentLastModificationDateLabel.setSizeUndefined();

		// tagy
		VerticalLayout tagsLayout = new VerticalLayout();
		layout.addComponent(tagsLayout);
		tagsLayout.addComponent(new Label("<h2>Tagy</h2>", ContentMode.HTML));

		tagsLayout.addComponent(tagsListLayout);
		tagsListLayout.setWidth("100%");

		// nástrojová lišta
		VerticalLayout operationsLayout = new VerticalLayout();
		layout.addComponent(operationsLayout);
		operationsLayout.addComponent(new Label("<h2>Operace s obsahem</h2>",
				ContentMode.HTML));

		operationsListLayout = new CssLayout();
		operationsLayout.addComponent(operationsListLayout);
		operationsListLayout.addStyleName("tools_css_menu");

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

		layout.addComponent(breadcrumb = new Breadcrumb());

		// Název obsahu
		VerticalLayout nameLayout = new VerticalLayout();
		layout.addComponent(nameLayout);
		nameLayout.addComponent(contentNameLabel = new Label());
		contentNameLabel.setContentMode(ContentMode.HTML);

		// samotný obsah
		createContent(layout);

		return layout;

	}

	protected abstract void createContent(VerticalLayout layout);

	protected abstract ContentNodeDTO getContentNodeDTO();

	private void updateBreadcrumb(ContentNodeDTO content) {

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();

		/**
		 * obsah
		 */
		breadcrumbElements.add(new BreadcrumbElement(content.getName(),
				getPageResource(
						pageFactory,
						URLIdentifierUtils.createURLIdentifier(
								content.getContentID(), content.getName()))));

		/**
		 * kategorie
		 */
		NodeFacade nodeFacade = NodeFacade.INSTANCE;
		NodeDTO parent = nodeFacade.getNodeById(content.getParentID());
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showError404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(),
					getPageResource(
							CategoryPage.FACTORY,
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

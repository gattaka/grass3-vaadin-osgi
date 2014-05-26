package org.myftp.gattserver.grass3.pages.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.subwindows.ContentMoveWindow;
import org.myftp.gattserver.grass3.subwindows.InfoWindow;
import org.myftp.gattserver.grass3.subwindows.WarnWindow;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public abstract class ContentViewerPage extends TwoColumnPage {

	private static final long serialVersionUID = 5078280973817331002L;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "tagPageFactory")
	private IPageFactory tagPageFactory;

	private ContentNodeDTO content;
	private Label contentNameLabel;
	private Label contentAuthorNameLabel;
	private Label contentCreationDateNameLabel;
	private Label contentLastModificationDateLabel;
	private CssLayout tagsListLayout;
	private CssLayout operationsListLayout;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	public ContentViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {

		tagsListLayout = new CssLayout();
		breadcrumb = new Breadcrumb();

		content = getContentNodeDTO();
		updateBreadcrumb(content);

		SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");

		contentNameLabel = new Label("<h2>" + content.getName() + "</h2>", ContentMode.HTML);

		contentAuthorNameLabel = new Label(content.getAuthor().getName());

		contentCreationDateNameLabel = new Label(dateFormat.format(content.getCreationDate()));

		contentLastModificationDateLabel = new Label(
				content.getLastModificationDate() == null ? "<em>-neupraveno-</em>" : dateFormat.format(content
						.getLastModificationDate()));
		contentLastModificationDateLabel.setContentMode(ContentMode.HTML);

		tagsListLayout.removeAllComponents();
		for (ContentTagDTO contentTag : content.getContentTags()) {
			Link tagLink = new Link(contentTag.getName(), getPageResource(tagPageFactory,
					URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName())));
			tagLink.addStyleName("taglabel");
			tagsListLayout.addComponent(tagLink);
		}

		operationsListLayout = new CssLayout();
		createContentOperations(operationsListLayout);

		super.init();
	}

	protected void createContentOperations(CssLayout operationsListLayout) {

		// Upravit
		if (coreACL.canModifyContent(content, getUser())) {
			Button modifyButton = new Button(null);
			modifyButton.setDescription("Upravit");
			modifyButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/pencil_16.png"));
			modifyButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {
					onEditOperation();
				}
			});
			operationsListLayout.addComponent(modifyButton);
		}

		// Smazat
		if (coreACL.canDeleteContent(content, getUser())) {
			Button deleteButton = new Button(null);
			deleteButton.setDescription("Smazat");
			deleteButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/delete_16.png"));
			deleteButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {
					onDeleteOperation();
				}
			});
			operationsListLayout.addComponent(deleteButton);
		}

		// Deklarace tlačítek oblíbených
		final Button removeFromFavouritesButton = new Button(null);
		final Button addToFavouritesButton = new Button(null);

		// Přidat do oblíbených
		addToFavouritesButton.setDescription("Přidat do oblíbených");
		addToFavouritesButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/heart_16.png"));
		addToFavouritesButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = -4003115363728232801L;

			public void buttonClick(ClickEvent event) {

				// zdařilo se ? Pokud ano, otevři info okno
				if (userFacade.addContentToFavourites(content, getUser())) {
					InfoWindow infoSubwindow = new InfoWindow("Vložení do oblíbených proběhlo úspěšně.");
					getUI().addWindow(infoSubwindow);
					addToFavouritesButton.setVisible(false);
					removeFromFavouritesButton.setVisible(true);
				} else {
					// Pokud ne, otevři warn okno
					WarnWindow warnSubwindow = new WarnWindow("Vložení do oblíbených se nezdařilo.");
					getUI().addWindow(warnSubwindow);
				}

			}
		});

		// Odebrat z oblíbených
		removeFromFavouritesButton.setDescription("Odebrat z oblíbených");
		removeFromFavouritesButton.setIcon((com.vaadin.server.Resource) new ThemeResource(
				"img/tags/broken_heart_16.png"));
		removeFromFavouritesButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4826586588570179321L;

			public void buttonClick(ClickEvent event) {

				// zdařilo se ? Pokud ano, otevři info okno
				if (userFacade.removeContentFromFavourites(content, getUser())) {
					InfoWindow infoSubwindow = new InfoWindow("Odebrání z oblíbených proběhlo úspěšně.");
					getUI().addWindow(infoSubwindow);
					removeFromFavouritesButton.setVisible(false);
					addToFavouritesButton.setVisible(true);
				} else {
					// Pokud ne, otevři warn okno
					WarnWindow warnSubwindow = new WarnWindow("Odebrání z oblíbených se nezdařilo.");
					getUI().addWindow(warnSubwindow);
				}

			}
		});

		// Oblíbené
		addToFavouritesButton.setVisible(coreACL.canAddContentToFavourites(content, getUser()));
		removeFromFavouritesButton.setVisible(coreACL.canRemoveContentFromFavourites(content, getUser()));

		operationsListLayout.addComponent(addToFavouritesButton);
		operationsListLayout.addComponent(removeFromFavouritesButton);

		// Změna kategorie
		if (coreACL.canModifyContent(content, getUser())) {
			Button moveButton = new Button(null);
			moveButton.setDescription("Přesunout");
			moveButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/move_16.png"));
			moveButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {
					UI.getCurrent().addWindow(new ContentMoveWindow(content) {
						private static final long serialVersionUID = 3748723613020816248L;

						@Override
						protected void onMove() {
							redirect(getPageURL(getContentViewerPageFactory(),
									URLIdentifierUtils.createURLIdentifier(content.getContentID(), content.getName())));
						}
					});
				}
			});
			operationsListLayout.addComponent(moveButton);
		}
	}

	@Override
	protected Component createLeftColumnContent() {

		CustomLayout layout = new CustomLayout("contentView");

		// info - přehled
		layout.addComponent(contentAuthorNameLabel, "author");
		layout.addComponent(contentCreationDateNameLabel, "createDate");
		layout.addComponent(contentLastModificationDateLabel, "modifyDate");

		if (content.isPublicated() == false) {
			HorizontalLayout publicatedLayout = new HorizontalLayout();
			publicatedLayout.setSpacing(true);
			publicatedLayout.setMargin(false);
			publicatedLayout.addStyleName("not-publicated-info");
			publicatedLayout.addComponent(new Embedded(null, new ThemeResource("img/tags/info_16.png")));
			publicatedLayout.addComponent(new Label("<strong>Nepublikováno</strong>", ContentMode.HTML));
			layout.addComponent(publicatedLayout, "pubinfo");
		}

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
		breadcrumbElements.add(new BreadcrumbElement(content.getName(), getPageResource(getContentViewerPageFactory(),
				URLIdentifierUtils.createURLIdentifier(content.getContentID(), content.getName()))));

		/**
		 * kategorie
		 */
		NodeDTO parent = content.getParent();
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showError404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(), getPageResource(categoryPageFactory,
					URLIdentifierUtils.createURLIdentifier(parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParent() == null)
				break;

			parent = parent.getParent();
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	protected abstract void onDeleteOperation();

	protected abstract void onEditOperation();
}

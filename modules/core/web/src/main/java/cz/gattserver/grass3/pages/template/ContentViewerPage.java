package cz.gattserver.grass3.pages.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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

import cz.gattserver.grass3.facades.INodeFacade;
import cz.gattserver.grass3.facades.IUserFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pages.template.TwoColumnPage;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.subwindows.ContentMoveWindow;
import cz.gattserver.grass3.subwindows.InfoWindow;
import cz.gattserver.grass3.subwindows.WarnWindow;
import cz.gattserver.grass3.template.AbstractButton;
import cz.gattserver.grass3.template.Breadcrumb;
import cz.gattserver.grass3.template.DeleteButton;
import cz.gattserver.grass3.template.ModifyButton;
import cz.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.grass3.util.URLIdentifierUtils;

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

	private Button removeFromFavouritesButton;
	private Button addToFavouritesButton;

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
			operationsListLayout.addComponent(new ModifyButton() {
				private static final long serialVersionUID = -491587087265983699L;

				@Override
				public void onClick(ClickEvent event) {
					onEditOperation();
				}
			});
		}

		// Smazat
		if (coreACL.canDeleteContent(content, getUser())) {
			operationsListLayout.addComponent(new DeleteButton() {
				private static final long serialVersionUID = -4293982845547453908L;

				@Override
				public void onClick(ClickEvent event) {
					onDeleteOperation();
				}
			});
		}

		// Oblíbené
		removeFromFavouritesButton = new AbstractButton("Odebrat z oblíbených", "img/tags/broken_heart_16.png") {
			private static final long serialVersionUID = 2867032632695180826L;

			@Override
			public void onClick(ClickEvent event) {
				// zdařilo se ? Pokud ano, otevři info okno
				if (userFacade.removeContentFromFavourites(content.getId(), getUser().getId())) {
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
		};

		addToFavouritesButton = new AbstractButton("Přidat do oblíbených", "img/tags/heart_16.png") {
			private static final long serialVersionUID = 2867032632695180826L;

			@Override
			public void onClick(ClickEvent event) {
				// zdařilo se ? Pokud ano, otevři info okno
				if (userFacade.addContentToFavourites(content.getId(), getUser().getId())) {
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
		};

		addToFavouritesButton.setVisible(coreACL.canAddContentToFavourites(content, getUser()));
		removeFromFavouritesButton.setVisible(coreACL.canRemoveContentFromFavourites(content, getUser()));

		operationsListLayout.addComponent(addToFavouritesButton);
		operationsListLayout.addComponent(removeFromFavouritesButton);

		// Změna kategorie
		if (coreACL.canModifyContent(content, getUser())) {
			operationsListLayout.addComponent(new AbstractButton("Přesunout", "img/tags/move_16.png") {
				private static final long serialVersionUID = 4009430146436270013L;

				@Override
				public void onClick(ClickEvent event) {
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

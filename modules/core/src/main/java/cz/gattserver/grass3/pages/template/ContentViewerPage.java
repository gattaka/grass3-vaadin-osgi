package cz.gattserver.grass3.pages.template;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.TwoColumnPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.subwindows.ContentMoveWindow;
import cz.gattserver.grass3.template.ImageButton;
import cz.gattserver.grass3.template.Breadcrumb;
import cz.gattserver.grass3.template.DeleteButton;
import cz.gattserver.grass3.template.ModifyButton;
import cz.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.WarnWindow;

public abstract class ContentViewerPage extends TwoColumnPage {

	@Autowired
	private CoreACL coreACL;

	@Autowired
	private UserFacade userFacade;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "tagPageFactory")
	private PageFactory tagPageFactory;

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

	@Override
	protected Layout createPayload() {
		tagsListLayout = new CssLayout();
		breadcrumb = new Breadcrumb();

		content = getContentNodeDTO();
		updateBreadcrumb(content);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm:ss");

		contentNameLabel = new H2Label(content.getName());
		contentAuthorNameLabel = new Label(content.getAuthor().getName());
		contentCreationDateNameLabel = new Label(
				content.getCreationDate() == null ? "" : content.getCreationDate().format(dateFormat));
		contentLastModificationDateLabel = new Label(content.getLastModificationDate() == null ? "<em>-neupraveno-</em>"
				: dateFormat.format(content.getLastModificationDate()));
		contentLastModificationDateLabel.setContentMode(ContentMode.HTML);

		tagsListLayout.removeAllComponents();
		for (ContentTagDTO contentTag : content.getContentTags()) {
			Link tagLink = new Link(contentTag.getName(), getPageResource(tagPageFactory,
					URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName())));
			tagLink.addStyleName("taglabel");
			tagsListLayout.addComponent(tagLink);
		}

		operationsListLayout = new CssLayout();
		if (!content.isDraft()) {
			createContentOperations(operationsListLayout);
		}

		JavaScript.eval("var pageScroll = document.getElementsByClassName('v-ui v-scrollable')[0]; "
				/*	*/ + "$(pageScroll).scroll(function() { "
				/*		*/ + "var height = $(pageScroll).scrollTop(); "
				/*		*/ + "if (height > 100) "
				/*			*/ + "document.getElementById('left').style['margin-top'] = (height - 100) + 'px'; "
				/*		*/ + "else "
				/*			*/ + "document.getElementById('left').style['margin-top'] = '0px'; "
				/*	*/ + "});");

		return super.createPayload();
	}

	protected void createContentOperations(CssLayout operationsListLayout) {
		// Upravit
		if (coreACL.canModifyContent(content, getUser())) {
			ModifyButton modBtn = new ModifyButton(event -> onEditOperation());
			operationsListLayout.addComponent(modBtn);
			modBtn.setIconAlternateText(modBtn.getCaption());
			modBtn.setCaption(null);
		}

		// Smazat
		if (coreACL.canDeleteContent(content, getUser())) {
			DeleteButton delBtn = new DeleteButton(event -> {
				onDeleteOperation();
			});
			operationsListLayout.addComponent(delBtn);
			delBtn.setIconAlternateText(delBtn.getCaption());
			delBtn.setCaption(null);
		}

		// Oblíbené
		removeFromFavouritesButton = new ImageButton(null, ImageIcons.BROKEN_HEART_16_ICON, event -> {
			// zdařilo se ? Pokud ano, otevři info okno
			try {
				userFacade.removeContentFromFavourites(content.getId(), getUser().getId());
				removeFromFavouritesButton.setVisible(false);
				addToFavouritesButton.setVisible(true);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno
				UI.getCurrent().addWindow(new WarnWindow("Odebrání z oblíbených se nezdařilo."));
			}
		});
		removeFromFavouritesButton.setIconAlternateText("Odebrat z oblíbených");

		addToFavouritesButton = new ImageButton(null, ImageIcons.HEART_16_ICON, event -> {
			// zdařilo se ? Pokud ano, otevři info okno
			try {
				userFacade.addContentToFavourites(content.getId(), getUser().getId());
				addToFavouritesButton.setVisible(false);
				removeFromFavouritesButton.setVisible(true);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno
				UI.getCurrent().addWindow(new WarnWindow("Vložení do oblíbených se nezdařilo."));
			}
		});
		addToFavouritesButton.setIconAlternateText("Přidat do oblíbených");

		addToFavouritesButton.setVisible(coreACL.canAddContentToFavourites(content, getUser()));
		removeFromFavouritesButton.setVisible(coreACL.canRemoveContentFromFavourites(content, getUser()));

		operationsListLayout.addComponent(addToFavouritesButton);
		operationsListLayout.addComponent(removeFromFavouritesButton);

		// Změna kategorie
		if (coreACL.canModifyContent(content, getUser())) {
			ImageButton moveBtn = new ImageButton(null, ImageIcons.MOVE_16_ICON, event -> {
				UI.getCurrent().addWindow(new ContentMoveWindow(content) {
					private static final long serialVersionUID = 3748723613020816248L;

					@Override
					protected void onMove() {
						redirect(getPageURL(getContentViewerPageFactory(),
								URLIdentifierUtils.createURLIdentifier(content.getContentID(), content.getName())));
					}
				});
			});
			operationsListLayout.addComponent(moveBtn);
			moveBtn.setIconAlternateText("Přesunout");
		}
	}

	@Override
	protected Component createLeftColumnContent() {

		CustomLayout layout = new CustomLayout("contentView");

		// info - přehled
		layout.addComponent(contentAuthorNameLabel, "author");
		layout.addComponent(contentCreationDateNameLabel, "createDate");
		layout.addComponent(contentLastModificationDateLabel, "modifyDate");

		if (!content.isPublicated()) {
			HorizontalLayout publicatedLayout = new HorizontalLayout();
			publicatedLayout.setSpacing(true);
			publicatedLayout.setMargin(false);
			publicatedLayout.addStyleName("not-publicated-info");
			publicatedLayout.addComponent(new Embedded(null, new ThemeResource(ImageIcons.INFO_16_ICON)));
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
		VerticalLayout marginlayout = new VerticalLayout();
		marginlayout.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		marginlayout.addComponent(layout);

		layout.setMargin(true);
		layout.setSpacing(true);

		layout.addComponent(breadcrumb);
		layout.addComponent(contentNameLabel);

		// samotný obsah
		createContent(layout);

		return marginlayout;
	}

	protected abstract void createContent(VerticalLayout layout);

	protected abstract ContentNodeDTO getContentNodeDTO();

	protected abstract PageFactory getContentViewerPageFactory();

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
		NodeBreadcrumbDTO parent = content.getParent();
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				showErrorPage404();

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(), getPageResource(nodePageFactory,
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

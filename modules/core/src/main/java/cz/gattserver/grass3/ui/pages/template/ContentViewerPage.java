package cz.gattserver.grass3.ui.pages.template;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.services.UserService;
import cz.gattserver.grass3.ui.components.Breadcrumb;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ImageButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.grass3.ui.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.TwoColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.grass3.ui.windows.ContentMoveDialog;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.BoldSpan;
import cz.gattserver.web.common.ui.HtmlSpan;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.WarnDialog;

public abstract class ContentViewerPage extends TwoColumnPage {

	private static final long serialVersionUID = -1564043277444025560L;

	@Autowired
	protected UserService userFacade;

	@Resource(name = "tagPageFactory")
	protected PageFactory tagPageFactory;

	private ContentNodeTO content;
	private H2 contentNameLabel;
	private Span contentAuthorNameLabel;
	private Span contentCreationDateNameLabel;
	private Span contentLastModificationDateLabel;
	private Div tagsListLayout;
	private Div operationsListLayout;

	private ImageButton removeFromFavouritesButton;
	private ImageButton addToFavouritesButton;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	@Override
	protected Div createPayload() {
		tagsListLayout = new Div();
		breadcrumb = new Breadcrumb();

		content = getContentNodeDTO();
		updateBreadcrumb(content);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm:ss");

		contentNameLabel = new H2(content.getName());
		contentAuthorNameLabel = new Span(content.getAuthor().getName());
		contentCreationDateNameLabel = new HtmlSpan(
				content.getCreationDate() == null ? "" : content.getCreationDate().format(dateFormat));
		contentLastModificationDateLabel = new HtmlSpan(content.getLastModificationDate() == null
				? "<em>-neupraveno-</em>" : dateFormat.format(content.getLastModificationDate()));

		tagsListLayout.removeAll();
		for (ContentTagOverviewTO contentTag : content.getContentTags()) {
			Anchor tagLink = new Anchor(getPageURL(tagPageFactory,
					URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()),
					contentTag.getName()));
			tagLink.addClassName("taglabel");
			tagsListLayout.add(tagLink);
		}

		operationsListLayout = new Div();
		if (!content.isDraft())
			createContentOperations(operationsListLayout);

		UI.getCurrent().getPage().executeJs("var pageScroll = document.getElementsByClassName('v-ui v-scrollable')[0]; "
				/*	*/ + "$(pageScroll).scroll(function() { "
				/*		*/ + "var height = $(pageScroll).scrollTop(); "
				/*		*/ + "if (height > 100) "
				/*			*/ + "document.getElementById('left').style['margin-top'] = (height - 100) + 'px'; "
				/*		*/ + "else "
				/*			*/ + "document.getElementById('left').style['margin-top'] = '0px'; "
				/*	*/ + "});");

		Div layout = super.createPayload();
		layout.addAttachListener(e -> UI.getCurrent().getPage().setTitle(content.getName()));
		layout.addDetachListener(e -> UI.getCurrent().getPage().setTitle("Gattserver"));
		return layout;
	}

	protected void createContentOperations(HasComponents operationsListLayout) {
		// Upravit
		if (coreACL.canModifyContent(content, getUser())) {
			ModifyButton modBtn = new ModifyButton(event -> onEditOperation());
			operationsListLayout.add(modBtn);
			modBtn.setTooltip(modBtn.getText());
			modBtn.setText(null);
		}

		// Smazat
		if (coreACL.canDeleteContent(content, getUser())) {
			DeleteButton delBtn = new DeleteButton(event -> onDeleteOperation());
			operationsListLayout.add(delBtn);
			delBtn.setTooltip(delBtn.getText());
			delBtn.setText(null);
		}

		// Oblíbené
		removeFromFavouritesButton = new ImageButton("Odebrat z oblíbených", ImageIcon.BROKEN_HEART_16_ICON, event -> {
			// zdařilo se ? Pokud ano, otevři info okno
			try {
				userFacade.removeContentFromFavourites(content.getId(), getUser().getId());
				removeFromFavouritesButton.setVisible(false);
				addToFavouritesButton.setVisible(true);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno
				new WarnDialog("Odebrání z oblíbených se nezdařilo.").open();
			}
		}).clearText();

		addToFavouritesButton = new ImageButton("Přidat do oblíbených", ImageIcon.HEART_16_ICON, event -> {
			// zdařilo se ? Pokud ano, otevři info okno
			try {
				userFacade.addContentToFavourites(content.getId(), getUser().getId());
				addToFavouritesButton.setVisible(false);
				removeFromFavouritesButton.setVisible(true);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno
				new WarnDialog("Vložení do oblíbených se nezdařilo.").open();
			}
		}).clearText();

		addToFavouritesButton.setVisible(coreACL.canAddContentToFavourites(content, getUser()));
		removeFromFavouritesButton.setVisible(coreACL.canRemoveContentFromFavourites(content, getUser()));

		operationsListLayout.add(addToFavouritesButton);
		operationsListLayout.add(removeFromFavouritesButton);

		// Změna kategorie
		if (coreACL.canModifyContent(content, getUser())) {
			ImageButton moveBtn = new ImageButton("Přesunout", ImageIcon.MOVE_16_ICON,
					event -> new ContentMoveDialog(content) {
						private static final long serialVersionUID = 3748723613020816248L;

						@Override
						protected void onMove() {
							UIUtils.redirect(getPageURL(getContentViewerPageFactory(),
									URLIdentifierUtils.createURLIdentifier(content.getContentID(), content.getName())));
						}
					}.open()).clearText();
			operationsListLayout.add(moveBtn);
		}
	}

	@Override
	protected Component createLeftColumnContent() {

		Div layout = new Div();
		layout.setClassName("left-content-view");

		// info - přehled
		layout.add(new H2("Info"));
		Div info = new Div();
		info.setClassName("content-info");
		layout.add(info);

		info.add(new BoldSpan("Autor:"));
		info.add(contentAuthorNameLabel);

		info.add(new BoldSpan("Vytvořeno:"));
		info.add(contentCreationDateNameLabel);

		info.add(new BoldSpan("Upraveno:"));
		info.add(contentLastModificationDateLabel);

		if (!content.isPublicated()) {
			HorizontalLayout publicatedLayout = new HorizontalLayout();
			publicatedLayout.setSpacing(true);
			publicatedLayout.setPadding(false);
			publicatedLayout.addClassName("not-publicated-info");
			publicatedLayout.add(new Image(ImageIcon.INFO_16_ICON.createResource(), "Info"));
			publicatedLayout.add(new BoldSpan("Nepublikováno"));
			info.add(publicatedLayout);
		}

		// tagy
		layout.add(new H2("Tagy"));
		Div tags = new Div();
		tags.setClassName("content-tags");
		layout.add(tags);
		tags.add(tagsListLayout);

		// nástrojová lišta
		layout.add(new H2("Operace s obsahem"));
		Div operations = new Div();
		operations.setClassName("content-operations");
		layout.add(operations);
		operations.add(operationsListLayout);

		return layout;

	}

	@Override
	protected Component createRightColumnContent() {
		VerticalLayout marginlayout = new VerticalLayout();
		marginlayout.setPadding(true);

		VerticalLayout layout = new VerticalLayout();
		marginlayout.add(layout);

		layout.setPadding(true);
		layout.setSpacing(true);

		layout.add(breadcrumb);
		layout.add(contentNameLabel);

		// samotný obsah
		createContent(layout);

		return marginlayout;
	}

	protected abstract void createContent(VerticalLayout layout);

	protected abstract ContentNodeTO getContentNodeDTO();

	protected abstract PageFactory getContentViewerPageFactory();

	private void updateBreadcrumb(ContentNodeTO content) {

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<>();

		/**
		 * obsah
		 */
		breadcrumbElements.add(new BreadcrumbElement(content.getName(), getPageURL(getContentViewerPageFactory(),
				URLIdentifierUtils.createURLIdentifier(content.getContentID(), content.getName()))));

		/**
		 * kategorie
		 */
		NodeTO parent = nodeFacade.getNodeByIdForDetail(content.getParent().getId());
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				throw new GrassPageException(404);

			breadcrumbElements.add(new BreadcrumbElement(parent.getName(), getPageURL(nodePageFactory,
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

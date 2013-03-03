package org.myftp.gattserver.grass3.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public abstract class BasePage extends GrassPage {

	private static final long serialVersionUID = 502625699429764791L;

	@Resource(name = "nodeFacade")
	private NodeFacade nodeFacade;

	@Resource(name = "quotesFacade")
	private QuotesFacade quotesFacade;

	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;

	@Resource(name = "categoryPageFactory")
	private PageFactory categoryPageFactory;

	@Resource(name = "quotesPageFactory")
	private PageFactory quotesPageFactory;

	@Resource(name = "loginPageFactory")
	private PageFactory loginPageFactory;

	@Resource(name = "registrationPageFactory")
	private PageFactory registrationPageFactory;

	@Resource(name = "settingsPageFactory")
	private PageFactory settingsPageFactory;

	private HorizontalLayout sectionsMenuLayout;
	private HorizontalLayout userMenuLayout;

	private GrassRequest request;

	public BasePage(GrassRequest request) {
		this.request = request;
	}

	@Override
	protected void createQuotes(CustomLayout layout) {

		// hlášky
		Link quotes = new Link();
		quotes.setResource(getPageResource(quotesPageFactory));
		quotes.setStyleName("quote");
		quotes.setCaption(chooseQuote());

		layout.addComponent(quotes, "quote");
	}

	@Override
	protected void createMenu(CustomLayout layout) {

		CustomLayout menu = new CustomLayout("menu");
		layout.addComponent(menu, "menu");

		// menu
		createSectionsMenu(menu);
		createUserMenu(menu);
	}

	protected GrassRequest getRequest() {
		return request;
	}

	private void createSectionsMenu(CustomLayout layout) {
		sectionsMenuLayout = new HorizontalLayout();
		layout.addComponent(sectionsMenuLayout, "sectionsmenu");

		sectionsMenuLayout.removeAllComponents();

		// link na domovskou stránku
		createHomeLink();

		// sekce článků je rozbalená rovnou jako její kořenové kategorie
		List<NodeDTO> nodes = nodeFacade.getRootNodes();
		for (NodeDTO node : nodes) {
			createCategoryLink(node.getName(),
					node.getId() + "-" + node.getName());
		}

		// externí sekce
		CoreACL acl = getUserACL();
		for (ISectionService section : ServiceHolder.getSectionServices()) {
			if (acl.canShowSection(section)) {
				createSectionLink(section.getSectionCaption(),
						section.getSectionPageFactory());
			}
		}
	}

	private void createUserMenu(CustomLayout layout) {
		userMenuLayout = new HorizontalLayout();
		layout.addComponent(userMenuLayout, "usermenu");

		userMenuLayout.removeAllComponents();

		CoreACL acl = getUserACL();

		// Přihlášení
		if (acl.canLogin()) {
			Link link = new Link("Přihlášení",
					getPageResource(loginPageFactory));
			link.setStyleName("item");
			userMenuLayout.addComponent(link);
		}

		// Registrace
		if (acl.canRegistrate()) {
			Link link = new Link("Registrace",
					getPageResource(registrationPageFactory));
			link.setStyleName("item");
			userMenuLayout.addComponent(link);
		}

		// Přehled o uživateli
		final UserInfoDTO userInfoDTO = getGrassUI().getUser();
		if (acl.canShowUserDetails(userInfoDTO)) {
			Button userDetails = new Button(userInfoDTO.getName(),
					new Button.ClickListener() {

						private static final long serialVersionUID = 4570994816815405844L;

						public void buttonClick(ClickEvent event) {
							final Window subwindow = new GrassSubWindow(
									"Detail uživatele " + userInfoDTO.getName());
							subwindow.center();
							getUI().addWindow(subwindow);
							subwindow.setWidth("220px");
							GridLayout gridLayout = new GridLayout(2, 4);
							gridLayout.setMargin(true);
							gridLayout.setSpacing(true);
							gridLayout.setSizeFull();
							subwindow.setContent(gridLayout);

							// Jméno
							gridLayout.addComponent(new Label("<h2>"
									+ userInfoDTO.getName() + "</h2>",
									ContentMode.HTML), 0, 0, 1, 0);

							// Admin ?
							gridLayout.addComponent(new Label("Admin"), 0, 1);
							gridLayout.addComponent(new Label(userInfoDTO
									.getRoles().contains(Role.ADMIN) ? "Ano"
									: "Ne"), 1, 1);

							// Friend ?
							gridLayout.addComponent(new Label("Friend"), 0, 2);
							gridLayout.addComponent(new Label(userInfoDTO
									.getRoles().contains(Role.FRIEND) ? "Ano"
									: "Ne"), 1, 2);

							// Author ?
							gridLayout.addComponent(new Label("Author"), 0, 3);
							gridLayout.addComponent(new Label(userInfoDTO
									.getRoles().contains(Role.AUTHOR) ? "Ano"
									: "Ne"), 1, 3);

							subwindow.focus();
						}
					});

			userDetails.setStyleName("user_status");
			userDetails.addStyleName(BaseTheme.BUTTON_LINK);
			userMenuLayout.addComponent(userDetails);

			// separator
			Label separator = new Label("|");
			separator.setStyleName("item");
			userMenuLayout.addComponent(separator);

			// nastavení
			Link link = new Link("Nastavení",
					getPageResource(settingsPageFactory));
			link.setStyleName("item");
			userMenuLayout.addComponent(link);

			// odhlásit
			Button button = new Button("Odhlásit", new Button.ClickListener() {

				private static final long serialVersionUID = 4570994816815405844L;

				public void buttonClick(ClickEvent event) {
					getGrassUI().logout();
				}
			});
			button.setStyleName(BaseTheme.BUTTON_LINK);
			button.addStyleName("item");
			userMenuLayout.addComponent(button);

		}
	}

	private String chooseQuote() {
		String quote = quotesFacade.getRandomQuote();
		if (quote == null) {
			showError500();
		}
		return quote;
	}

	private void createHomeLink() {
		Link link = new Link("Domů", getPageResource(homePageFactory));
		link.setStyleName("item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createSectionLink(String caption, PageFactory pageFactory) {
		Link link = new Link(caption, getPageResource(pageFactory));
		link.setStyleName("item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createCategoryLink(String caption, String URL) {
		Link link = new Link(caption, getPageResource(categoryPageFactory, URL));
		link.setStyleName("item");
		sectionsMenuLayout.addComponent(link);
	}

}

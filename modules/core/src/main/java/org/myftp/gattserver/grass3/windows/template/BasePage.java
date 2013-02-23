package org.myftp.gattserver.grass3.windows.template;

import java.util.List;

import org.myftp.gattserver.grass3.GrassUI;
import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.CategoryWindow;
import org.myftp.gattserver.grass3.windows.HomePage.HomePageFactory;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginPage.LoginPageFactory;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesPage.QuotesPageFactory;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.RegistrationPage.RegistrationPageFactory;
import org.myftp.gattserver.grass3.windows.RegistrationWindow;
import org.myftp.gattserver.grass3.windows.template.SettingsPage.SettingsPageFactory;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public abstract class BasePage extends GrassPage {

	private static final long serialVersionUID = 502625699429764791L;

	private QuotesFacade quotesFacade = QuotesFacade.INSTANCE;
	protected NodeFacade nodeFacade = NodeFacade.INSTANCE;

	private CssLayout sectionsMenuLayout = new CssLayout();
	private CssLayout userMenuLayout = new CssLayout();

	private GrassRequest request;

	public BasePage(GrassRequest request) {
		super("base");

		this.request = request;

		// homelink (přes logo)
		Link homelink = new Link();
		homelink.addStyleName("homelink");
		homelink.setResource(getPageResource(HomePageFactory.INSTANCE));
		homelink.setIcon(new ThemeResource("img/logo.png"));
		addComponent(homelink, "homelink");

		// hlášky
		Link quotes = new Link();
		quotes.setResource(getPageResource(QuotesPageFactory.INSTANCE));
		quotes.setStyleName("quotes");
		quotes.setCaption(chooseQuote());
		addComponent(quotes, "quote");

		// menu
		createSectionsMenu(this);
		createUserMenu(this);

		// obsah
		createWindowContent(this);

		// footer
		addComponent(new Label("GRASS3"), "about");
	}

	protected abstract void createContent(CustomLayout layout);

	protected GrassRequest getRequest() {
		return request;
	}

	private void createSectionsMenu(CustomLayout layout) {
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
		for (ISectionService section : ServiceHolder.getInstance()
				.getSectionServices()) {
			if (acl.canShowSection(section)) {
				createSectionLink(section.getSectionCaption(),
						section.getSectionWindowClass());
			}
		}

		String[] strings = { "Domů", "Sekce", "Delší název sekce", "Něco",
				"Něco dalšího", "Poslední" };
		for (int i = 0; i < strings.length; i++) {
			Label item = new Label(strings[i]);
			item.addStyleName("item");
			item.setSizeUndefined();
			sectionsMenuLayout.addComponent(item);
		}
	}

	private void createUserMenu(CustomLayout layout) {
		layout.addComponent(userMenuLayout, "usermenu");

		userMenuLayout.removeAllComponents();

		CoreACL acl = getUserACL();

		// Přihlášení
		if (acl.canLogin()) {
			Link link = new Link("Přihlášení",
					getPageResource(LoginPageFactory.INSTANCE));
			link.setStyleName("item");
			userMenuLayout.addComponent(link);
		}

		// Registrace
		if (acl.canRegistrate()) {
			Link link = new Link("Registrace",
					getPageResource(RegistrationPageFactory.INSTANCE));
			link.setStyleName("item");
			userMenuLayout.addComponent(link);
		}

		// Přehled o uživateli
		final UserInfoDTO userInfoDTO = ((GrassUI) getUI()).getUser();
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
			userDetails.addStyleName("menu_item");
			userDetails.addStyleName(BaseTheme.BUTTON_LINK);
			userMenuLayout.addComponent(userDetails);

			// separator
			Label separator = new Label("|");
			separator.setStyleName("item");
			userMenuLayout.addComponent(separator);

			// nastavení
			Link link = new Link("Nastavení",
					getPageResource(SettingsPageFactory.INSTANCE));
			link.setStyleName("item");
			userMenuLayout.addComponent(link);

			// odhlásit
			Button button = new Button("Odhlásit", new Button.ClickListener() {

				private static final long serialVersionUID = 4570994816815405844L;

				public void buttonClick(ClickEvent event) {
					getApplication().close();
				}
			});
			button.setStyleName(BaseTheme.BUTTON_LINK);
			button.addStyleName("item");
			userMenuLayout.addComponent(button);

		}

		String[] strings = { "Uživatel", "Nastavení", "Odhlásit" };
		for (int i = 0; i < strings.length; i++) {
			Label item = new Label(strings[i]);
			item.addStyleName("item");
			item.setSizeUndefined();
			userMenuLayout.addComponent(item);
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
		Link link = new Link("Domů", getWindowResource(HomeWindow.class));
		link.setStyleName("item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createSectionLink(String caption,
			Class<? extends GrassWindow> windowClass) {
		Link link = new Link(caption, getWindowResource(windowClass));
		link.setStyleName("item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createCategoryLink(String caption, String URL) {
		Link link = new Link(caption, new ExternalResource(getWindow(
				CategoryWindow.class).getURL()
				+ URL));
		link.setStyleName("item");
		sectionsMenuLayout.addComponent(link);
	}

	/**
	 * Vytvoří obsah okna - tedy všechno to, co je mezi menu a footerem
	 * 
	 * @param layout
	 *            layout, do kterého se má vytvářet
	 */
	protected abstract void createWindowContent(CustomLayout layout);

}

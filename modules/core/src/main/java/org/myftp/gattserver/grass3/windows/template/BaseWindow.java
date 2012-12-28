package org.myftp.gattserver.grass3.windows.template;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.security.ACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.windows.CategoryWindow;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.RegistrationWindow;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public abstract class BaseWindow extends BackgroundWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private QuotesFacade quotesFacade = QuotesFacade.INSTANCE;
	protected NodeFacade nodeFacade = NodeFacade.INSTANCE;

	private HorizontalLayout sectionsMenuLayout = new HorizontalLayout();
	private HorizontalLayout userMenuLayout = new HorizontalLayout();
	private Link quotes;

	private Set<String> initJS = new LinkedHashSet<String>();

	/**
	 * Přihlásí skripty
	 * 
	 * @param initJS
	 */
	protected void submitInitJS(Set<String> initJS) {
		initJS.add("/VAADIN/themes/grass/js/grass.js");
	}

	/**
	 * Vezme všechen nahlášený JS init obsah a provede ho (kaskádově s ohledem
	 * na závislosti)
	 */
	private void gatherInitJS() {

		StringBuilder loadScript = new StringBuilder();

		// nejprve jQuery
		loadScript
				.append("var head= document.getElementsByTagName('head')[0];")
				.append("var script= document.createElement('script');")
				.append("script.type= 'text/javascript';")
				.append("script.src= '/VAADIN/themes/grass/js/jquery.js';")
				.append("var callback = function() {");

		// ostatní JS už lze nahrávat pomocí jQuery
		for (String js : initJS) {
			loadScript.append("$.getScript('" + js + "', function(){");
		}
		// uzavřít
		for (int i = 0; i < initJS.size(); i++) {
			loadScript.append("});");
		}

		// konec jQuery
		loadScript.append("};").append("script.onreadystatechange = callback;")
				.append("script.onload = callback;")
				.append("head.appendChild(script);");

		// fire !
		executeJavaScript(loadScript.toString());

	}

	@Override
	protected void onShow() {

		submitInitJS(initJS);
		gatherInitJS();

		// update menu sekcí
		populateSectionsMenu();

		// update menu uživatele
		populateUserMenu();

		// update hlášek
		quotes.setCaption(chooseQuote());

	}

	private void populateSectionsMenu() {
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
		for (ISectionService section : ServiceHolder.getInstance()
				.getSectionServices()) {
			createSectionLink(section.getSectionCaption(),
					section.getSectionWindowClass());
		}
	}

	private void populateUserMenu() {
		userMenuLayout.removeAllComponents();

		final UserInfoDTO userInfoDTO = getApplication().getUser();
		if (userInfoDTO == null) {
			Link link = new Link("Přihlášení",
					getWindowResource(LoginWindow.class));
			link.setStyleName("menu_item");
			userMenuLayout.addComponent(link);

			// Registrovat
			if (ACL.get(userInfoDTO).canRegistrate()) {
				link = new Link("Registrace",
						getWindowResource(RegistrationWindow.class));
				link.setStyleName("menu_item");
				userMenuLayout.addComponent(link);
			}

		} else {

			Button userDetails = new Button(userInfoDTO.getName(),
					new Button.ClickListener() {

						private static final long serialVersionUID = 4570994816815405844L;

						public void buttonClick(ClickEvent event) {
							final Window subwindow = new GrassSubWindow(
									"Detail uživatele " + userInfoDTO.getName());
							subwindow.center();
							addWindow(subwindow);
							subwindow.setWidth("220px");
							GridLayout gridLayout = new GridLayout(2, 4);
							gridLayout.setMargin(true);
							gridLayout.setSpacing(true);
							gridLayout.setSizeFull();
							subwindow.setContent(gridLayout);

							// Jméno
							gridLayout.addComponent(new Label("<h2>"
									+ userInfoDTO.getName() + "</h2>",
									Label.CONTENT_XHTML), 0, 0, 1, 0);

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
			separator.setStyleName("menu_item");
			userMenuLayout.addComponent(separator);

			// nastavení
			Link link = new Link("Nastavení",
					getWindowResource(SettingsWindow.class));
			link.setStyleName("menu_item");
			userMenuLayout.addComponent(link);

			// odhlásit
			Button button = new Button("Odhlásit", new Button.ClickListener() {

				private static final long serialVersionUID = 4570994816815405844L;

				public void buttonClick(ClickEvent event) {
					getApplication().close();
				}
			});
			button.setStyleName(BaseTheme.BUTTON_LINK);
			button.addStyleName("menu_item");
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

	protected void buildHeader(HorizontalLayout headerLayout) {

		// Hlášky - generují se znova a znova
		quotes = new Link(chooseQuote(), getWindowResource(QuotesWindow.class));
		quotes.setStyleName("quotes");
		quotes.setWidth("740px");
		headerLayout.addComponent(quotes);
	}

	protected void buildBody(VerticalLayout layout) {

		VerticalLayout bodyLayout = new VerticalLayout();
		layout.addComponent(bodyLayout);
		layout.setComponentAlignment(bodyLayout, Alignment.TOP_CENTER);

		bodyLayout.setStyleName("body_layout");
		bodyLayout.setWidth("990px");
		bodyLayout.setMargin(false, false, true, false);

		// menu stránky
		createMenu(bodyLayout);

		// obsah stránky
		createWindowContent(bodyLayout);

	}

	private void createMenu(VerticalLayout layout) {

		// menu (centrovací element)
		HorizontalLayout menuHolderLayout = new HorizontalLayout();

		layout.addComponent(menuHolderLayout);
		menuHolderLayout.setStyleName("menu_holder");
		menuHolderLayout.setWidth("990px");
		menuHolderLayout.setHeight("41px");
		menuHolderLayout.setMargin(false, true, false, true);

		// sekce menu
		createSectionsMenu(menuHolderLayout);

		// user menu
		createUserMenu(menuHolderLayout);
	}

	private void createHomeLink() {
		Link link = new Link("Domů", getWindowResource(HomeWindow.class));
		link.setStyleName("first_menu_item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createSectionsMenu(HorizontalLayout layout) {
		layout.addComponent(sectionsMenuLayout);
		layout.setComponentAlignment(sectionsMenuLayout, Alignment.MIDDLE_LEFT);
		sectionsMenuLayout.setStyleName("sections_menu_layout");

		// Domů
		createHomeLink();
	}

	private void createSectionLink(String caption,
			Class<? extends GrassWindow> windowClass) {
		Link link = new Link(caption, getWindowResource(windowClass));
		link.setStyleName("menu_item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createCategoryLink(String caption, String URL) {
		Link link = new Link(caption, new ExternalResource(getWindow(
				CategoryWindow.class).getURL()
				+ URL));
		link.setStyleName("menu_item");
		sectionsMenuLayout.addComponent(link);
	}

	private void createUserMenu(HorizontalLayout layout) {
		layout.addComponent(userMenuLayout);
		layout.setComponentAlignment(userMenuLayout, Alignment.MIDDLE_RIGHT);
		userMenuLayout.setStyleName("user_menu_layout");
	}

	/**
	 * Vytvoří obsah okna - tedy všechno to, co je mezi menu a footerem
	 * 
	 * @param layout
	 *            layout, do kterého se má vytvářet
	 */
	protected abstract void createWindowContent(VerticalLayout layout);

}

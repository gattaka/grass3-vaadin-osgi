package cz.gattserver.grass3.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.ServiceHolder;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.SectionService;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.window.WebWindow;

public abstract class BasePage extends AbstractGrassPage {

	private static final long serialVersionUID = 502625699429764791L;

	@Autowired
	protected CoreACL coreACL;

	@Autowired
	protected NodeFacade nodeFacade;

	@Autowired
	protected QuotesFacade quotesFacade;

	@Resource(name = "homePageFactory")
	protected PageFactory homePageFactory;

	@Resource(name = "nodePageFactory")
	protected PageFactory nodePageFactory;

	@Resource(name = "quotesPageFactory")
	protected PageFactory quotesPageFactory;

	@Resource(name = "loginPageFactory")
	protected PageFactory loginPageFactory;

	@Resource(name = "registrationPageFactory")
	protected PageFactory registrationPageFactory;

	@Resource(name = "settingsPageFactory")
	protected PageFactory settingsPageFactory;

	@Autowired
	protected ServiceHolder serviceHolder;

	public BasePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createQuotes(CustomLayout layout) {

		// hlášky
		Link quotes = new Link();
		quotes.setResource(getPageResource(quotesPageFactory));
		quotes.setStyleName("quote");
		quotes.setCaption("\"" + chooseQuote() + "\"");

		layout.addComponent(quotes, "quote");
	}

	private void createMenuComponent(HorizontalLayout menu, Component component) {
		menu.addComponent(component);
		component.addStyleName("menu-item");
	}

	private void createNewMenu(HorizontalLayout menu) {

		/**
		 * Sections menu
		 */

		// sekce článků je rozbalená rovnou jako její kořenové kategorie
		List<NodeDTO> nodes = nodeFacade.getRootNodes();
		for (NodeDTO node : nodes) {
			createMenuComponent(menu,
					new Link(node.getName(), getPageResource(nodePageFactory, node.getId() + "-" + node.getName())));
		}

		// externí sekce
		for (SectionService section : serviceHolder.getSectionServices()) {
			if (coreACL.canShowSection(section, getUser())) {
				createMenuComponent(menu,
						new Link(section.getSectionCaption(), getPageResource(section.getSectionPageFactory())));
			}
		}

		Label sep = new Label();
		menu.addComponent(sep);
		menu.setExpandRatio(sep, 1);

		/**
		 * User menu
		 */

		// Přihlášení
		if (!coreACL.isLoggedIn(getUser())) {
			createMenuComponent(menu, new Link("Přihlášení", getPageResource(loginPageFactory)));
		}

		// Registrace
		// if (coreACL.canRegistrate(getUser())) {
		// createMenuComponent(menu, new Link("Registrace",
		// getPageResource(registrationPageFactory)));
		// }

		// Přehled o uživateli
		final UserInfoDTO userInfoDTO = getGrassUI().getUser();
		if (coreACL.canShowUserDetails(userInfoDTO, getUser())) {

			Button userDetailsButton = new Button(userInfoDTO.getName(), event -> {
				final Window subwindow = new WebWindow("Detail uživatele " + userInfoDTO.getName());
				subwindow.center();
				getUI().addWindow(subwindow);
				subwindow.setWidth("220px");
				subwindow.setHeight("260px");
				GridLayout gridLayout = new GridLayout(2, 4);
				gridLayout.setMargin(true);
				gridLayout.setSpacing(true);
				gridLayout.setSizeFull();
				subwindow.setContent(gridLayout);

				// Jméno
				gridLayout.addComponent(new Label("<h2>" + userInfoDTO.getName() + "</h2>", ContentMode.HTML), 0, 0, 1,
						0);

				// Admin ?
				gridLayout.addComponent(new Label("Admin"), 0, 1);
				gridLayout.addComponent(new Label(userInfoDTO.getRoles().contains(Role.ADMIN) ? "Ano" : "Ne"), 1, 1);

				// Friend ?
				gridLayout.addComponent(new Label("Friend"), 0, 2);
				gridLayout.addComponent(new Label(userInfoDTO.getRoles().contains(Role.FRIEND) ? "Ano" : "Ne"), 1, 2);

				// Author ?
				gridLayout.addComponent(new Label("Author"), 0, 3);
				gridLayout.addComponent(new Label(userInfoDTO.getRoles().contains(Role.AUTHOR) ? "Ano" : "Ne"), 1, 3);

				subwindow.focus();
			});
			userDetailsButton.setStyleName(ValoTheme.BUTTON_LINK);
			userDetailsButton.addStyleName("user_status");
			createMenuComponent(menu, userDetailsButton);

			// separator
			Label separator = new Label("|");
			createMenuComponent(menu, separator);

			// nastavení
			createMenuComponent(menu, new Link("Nastavení", getPageResource(settingsPageFactory)));

			// odhlásit
			Button logOffButton = new Button("Odhlásit", event -> {
				SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(null);
				SecurityContextHolder.clearContext();
				getSession().close();
				Page.getCurrent().reload();
			});
			logOffButton.setStyleName(ValoTheme.BUTTON_LINK);
			createMenuComponent(menu, logOffButton);
		}
	}

	@Override
	protected void createMenu(CustomLayout layout) {

		// CustomLayout menu = new CustomLayout("menu");
		HorizontalLayout menuExpander = new HorizontalLayout();
		menuExpander.setWidth("970px");
		// menuExpander.setHeight("70px");
		menuExpander.addStyleName("menu");
		menuExpander.setMargin(false);
		menuExpander.setSpacing(false);
		layout.addComponent(menuExpander, "menu");

		HorizontalLayout menu = new HorizontalLayout();
		menu.setSpacing(true);
		// menu.addStyleName("v-menubar");
		menu.setWidth("100%");
		menuExpander.addComponent(menu);

		createNewMenu(menu);
	}

	private String chooseQuote() {
		String quote = quotesFacade.getRandomQuote();
		if (quote == null) {
			showError500();
		}
		return quote;
	}

}

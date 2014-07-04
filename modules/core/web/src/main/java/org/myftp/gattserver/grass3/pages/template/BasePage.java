package org.myftp.gattserver.grass3.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.IServiceHolder;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.facades.IQuotesFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.subwindows.GrassWindow;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Window;

public abstract class BasePage extends AbstractGrassPage {

	private static final long serialVersionUID = 502625699429764791L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "quotesFacade")
	private IQuotesFacade quotesFacade;

	@Resource(name = "homePageFactory")
	private IPageFactory homePageFactory;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "quotesPageFactory")
	private IPageFactory quotesPageFactory;

	@Resource(name = "loginPageFactory")
	private IPageFactory loginPageFactory;

	@Resource(name = "registrationPageFactory")
	private IPageFactory registrationPageFactory;

	@Resource(name = "settingsPageFactory")
	private IPageFactory settingsPageFactory;

	@Resource(name = "serviceHolder")
	private IServiceHolder serviceHolder;

	public BasePage(GrassRequest request) {
		super(request);
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

	private void createMenuComponent(HorizontalLayout menu, Component component) {
		menu.addComponent(component);
		component.addStyleName("menu-item");
	}

	private void createNewMenu(HorizontalLayout menu) {

		/**
		 * Sections menu
		 */
		// link na domovskou stránku
		// menu.addComponent(new Link("Domů",
		// getPageResource(homePageFactory)));

		// sekce článků je rozbalená rovnou jako její kořenové kategorie
		List<NodeDTO> nodes = nodeFacade.getRootNodes();
		for (NodeDTO node : nodes) {
			createMenuComponent(menu,
					new Link(node.getName(), getPageResource(categoryPageFactory, node.getId() + "-" + node.getName())));
		}

		// externí sekce
		for (ISectionService section : serviceHolder.getSectionServices()) {
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
		if (coreACL.canLogin(getUser())) {
			createMenuComponent(menu, new Link("Přihlášení", getPageResource(loginPageFactory)));
		}

		// Registrace
		if (coreACL.canRegistrate(getUser())) {
			createMenuComponent(menu, new Link("Registrace", getPageResource(registrationPageFactory)));
		}

		// Přehled o uživateli
		final UserInfoDTO userInfoDTO = getGrassUI().getUser();
		if (coreACL.canShowUserDetails(userInfoDTO, getUser())) {

			Button userDetailsButton = new Button(userInfoDTO.getName(), new Button.ClickListener() {

				private static final long serialVersionUID = 4570994816815405844L;

				@Override
				public void buttonClick(ClickEvent event) {
					final Window subwindow = new GrassWindow("Detail uživatele " + userInfoDTO.getName());
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
					gridLayout.addComponent(new Label("<h2>" + userInfoDTO.getName() + "</h2>", ContentMode.HTML), 0,
							0, 1, 0);

					// Admin ?
					gridLayout.addComponent(new Label("Admin"), 0, 1);
					gridLayout
							.addComponent(new Label(userInfoDTO.getRoles().contains(Role.ADMIN) ? "Ano" : "Ne"), 1, 1);

					// Friend ?
					gridLayout.addComponent(new Label("Friend"), 0, 2);
					gridLayout.addComponent(new Label(userInfoDTO.getRoles().contains(Role.FRIEND) ? "Ano" : "Ne"), 1,
							2);

					// Author ?
					gridLayout.addComponent(new Label("Author"), 0, 3);
					gridLayout.addComponent(new Label(userInfoDTO.getRoles().contains(Role.AUTHOR) ? "Ano" : "Ne"), 1,
							3);

					subwindow.focus();
				}
			});
			userDetailsButton.setStyleName(Reindeer.BUTTON_LINK);
			userDetailsButton.addStyleName("user_status");
			createMenuComponent(menu, userDetailsButton);

			// separator
			Label separator = new Label("|");
			createMenuComponent(menu, separator);

			// nastavení
			createMenuComponent(menu, userDetailsButton);

			// odhlásit
			Button logOffButton = new Button("Odhlásit", new Button.ClickListener() {
				private static final long serialVersionUID = 5161534666150825952L;

				@Override
				public void buttonClick(ClickEvent event) {
					redirect(getPageURL("j_spring_security_logout"));
				}
			});
			logOffButton.setStyleName(Reindeer.BUTTON_LINK);
			createMenuComponent(menu, logOffButton);
		}
	}

	@Override
	protected void createMenu(CustomLayout layout) {

		// CustomLayout menu = new CustomLayout("menu");
		HorizontalLayout menuExpander = new HorizontalLayout();
		menuExpander.setWidth("970px");
		menuExpander.setHeight("70px");
		menuExpander.addStyleName("menu");
		menuExpander.setMargin(false);
		menuExpander.setSpacing(false);
		layout.addComponent(menuExpander, "menu");

		HorizontalLayout menu = new HorizontalLayout();
		menu.setSpacing(true);
		menu.addStyleName("v-menubar");
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

	private MenuItem createCategoryPageMenuItem(MenuBar bar, String name, String url) {
		return bar.addItem(name, createCategoryCommand(url));
	}

	private MenuItem createPageMenuItem(MenuBar bar, String name, IPageFactory factory) {
		return bar.addItem(name, createCommand(factory));
	}

	private MenuItem createPageMenuItem(MenuItem item, String name, IPageFactory factory) {
		return item.addItem(name, createCommand(factory));
	}

	private Command createCommand(final IPageFactory factory) {
		return new Command() {
			private static final long serialVersionUID = 4072974580734348622L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				redirect(getPageURL(factory));
			}
		};
	}

	private Command createCategoryCommand(final String url) {
		return new Command() {
			private static final long serialVersionUID = 5148518087273073333L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				redirect(getPageURL(categoryPageFactory, url));
			}
		};
	}

}

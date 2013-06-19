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
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
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

	@Override
	protected void createMenu(CustomLayout layout) {

		// CustomLayout menu = new CustomLayout("menu");
		HorizontalLayout menu = new HorizontalLayout();
		menu.setWidth("970px");
		menu.setHeight("70px");
		menu.addStyleName("menu");
		menu.setMargin(false);
		menu.setSpacing(false);

		layout.addComponent(menu, "menu");

		MenuBar sectionsMenu = new MenuBar();
		MenuBar userMenu = new MenuBar();

		menu.addComponent(sectionsMenu);
		menu.addComponent(userMenu);

		sectionsMenu.setWidth("100%");
		menu.setExpandRatio(sectionsMenu, 1);

		/**
		 * Sections menu
		 */

		// link na domovskou stránku
		createPageMenuItem(sectionsMenu, "Domů", homePageFactory);

		// sekce článků je rozbalená rovnou jako její kořenové kategorie
		List<NodeDTO> nodes = nodeFacade.getRootNodes();
		for (NodeDTO node : nodes) {
			createCategoryPageMenuItem(sectionsMenu, node.getName(),
					node.getId() + "-" + node.getName());
		}

		MenuItem appsMenu = sectionsMenu.addItem("Aplikace", null);

		// externí sekce
		for (ISectionService section : serviceHolder.getSectionServices()) {
			if (coreACL.canShowSection(section, getUser())) {
				createPageMenuItem(appsMenu, section.getSectionCaption(),
						section.getSectionPageFactory());
			}
		}

		/**
		 * User menu
		 */

		// Přihlášení
		if (coreACL.canLogin(getUser())) {
			createPageMenuItem(userMenu, "Přihlášení", loginPageFactory);
		}

		// Registrace
		if (coreACL.canRegistrate(getUser())) {
			createPageMenuItem(userMenu, "Registrace", registrationPageFactory);
		}

		// Přehled o uživateli
		final UserInfoDTO userInfoDTO = getGrassUI().getUser();
		if (coreACL.canShowUserDetails(userInfoDTO, getUser())) {
			MenuItem userDetails = userMenu.addItem(userInfoDTO.getName(),
					new Command() {
						private static final long serialVersionUID = 4570994816815405844L;

						@Override
						public void menuSelected(MenuItem selectedItem) {

							final Window subwindow = new GrassSubWindow(
									"Detail uživatele " + userInfoDTO.getName());
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

			// separator
			MenuItem separator = userMenu.addItem("|", null);
			separator.setEnabled(false);

			// nastavení
			createPageMenuItem(userMenu, "Nastavení", settingsPageFactory);

			// odhlásit
			userMenu.addItem("Odhlásit", new Command() {
				private static final long serialVersionUID = 5161534666150825952L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					redirect(getPageURL("j_spring_security_logout"));
				}
			});
		}

	}

	private String chooseQuote() {
		String quote = quotesFacade.getRandomQuote();
		if (quote == null) {
			showError500();
		}
		return quote;
	}

	private MenuItem createCategoryPageMenuItem(MenuBar bar, String name,
			String url) {
		return bar.addItem(name, createCategoryCommand(url));
	}

	private MenuItem createPageMenuItem(MenuBar bar, String name,
			IPageFactory factory) {
		return bar.addItem(name, createCommand(factory));
	}

	private MenuItem createPageMenuItem(MenuItem item, String name,
			IPageFactory factory) {
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

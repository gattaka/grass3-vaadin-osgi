package cz.gattserver.grass3.ui.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.modules.register.ModuleRegister;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.util.UIUtils;

public abstract class MenuPage extends GrassPage {

	@Autowired
	protected ModuleRegister serviceHolder;

	@Autowired
	protected CoreACLService coreACL;

	@Autowired
	protected NodeService nodeFacade;

	@Resource(name = "homePageFactory")
	protected PageFactory homePageFactory;

	@Resource(name = "nodePageFactory")
	protected PageFactory nodePageFactory;

	@Resource(name = "loginPageFactory")
	protected PageFactory loginPageFactory;

	@Resource(name = "settingsPageFactory")
	protected PageFactory settingsPageFactory;

	@Resource(name = "registrationPageFactory")
	protected PageFactory registrationPageFactory;

	public MenuPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		CustomLayout layout = new CustomLayout("grass");
		layout.addStyleName("grasspage");

		// homelink (přes logo)
		Link homelink = new Link();
		homelink.addStyleName("homelink");
		homelink.setResource(getPageResource(homePageFactory));
		homelink.setIcon(new ThemeResource("img/logo.png"));
		layout.addComponent(homelink, "homelink");

		// hlášky
		createQuotes(layout);

		// menu
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

		createMenuItems(menu);

		// obsah
		createContent(layout);

		// footer
		layout.addComponent(new Label("Powered by GRASS III © 2012-2017 Hynek Uhlíř"), "about");

		return layout;
	}

	protected void createMenuComponent(HorizontalLayout menu, Component component) {
		menu.addComponent(component);
		component.addStyleName("menu-item");
	}

	/**
	 * Získá menu
	 */
	protected void createMenuItems(HorizontalLayout menu) {

		/**
		 * Sections menu
		 */

		// sekce článků je rozbalená rovnou jako její kořenové kategorie
		List<NodeOverviewTO> nodes = nodeFacade.getRootNodes();
		for (NodeOverviewTO node : nodes) {
			createMenuComponent(menu,
					new Link(node.getName(), getPageResource(nodePageFactory, node.getId() + "-" + node.getName())));
		}

		// externí sekce
		for (SectionService section : serviceHolder.getSectionServices()) {
			if (coreACL.canShowSection(section, UIUtils.getUser())) {
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
		if (!coreACL.isLoggedIn(UIUtils.getUser())) {
			createMenuComponent(menu, new Link("Přihlášení", getPageResource(loginPageFactory)));
		}

		// Registrace
		// if (coreACL.canRegistrate(getUser())) {
		// createMenuComponent(menu, new Link("Registrace",
		// getPageResource(registrationPageFactory)));
		// }

		// Přehled o uživateli
		final UserInfoTO userInfoDTO = UIUtils.getGrassUI().getUser();
		if (coreACL.canShowUserDetails(userInfoDTO, UIUtils.getUser())) {
			// nastavení
			createMenuComponent(menu, new Link(userInfoDTO.getName(), getPageResource(settingsPageFactory)));

			// odhlásit
			createMenuComponent(menu, new Link("Odhlásit", new ExternalResource(getPageURL("logout"))));
		}
	}

	/**
	 * Získá hlášky
	 */
	protected abstract void createQuotes(CustomLayout layout);

	/**
	 * Získá obsah
	 */
	protected abstract void createContent(CustomLayout layout);

}
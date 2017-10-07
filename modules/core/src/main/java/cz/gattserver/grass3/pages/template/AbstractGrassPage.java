package cz.gattserver.grass3.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import cz.gattserver.grass3.ServiceHolder;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.service.SectionService;
import cz.gattserver.grass3.ui.util.GrassRequest;

public abstract class AbstractGrassPage extends GrassLayout implements GrassPage {

	private static final long serialVersionUID = 604170960797872356L;

	@Autowired
	protected ServiceHolder serviceHolder;

	@Autowired
	protected CoreACL coreACL;

	@Autowired
	protected NodeFacade nodeFacade;

	@Resource(name = "homePageFactory")
	protected PageFactory homePageFactory;

	@Resource(name = "nodePageFactory")
	protected PageFactory nodePageFactory;

	@Resource(name = "loginPageFactory")
	protected PageFactory loginPageFactory;

	@Resource(name = "logoutPageFactory")
	protected PageFactory logoutPageFactory;

	@Resource(name = "settingsPageFactory")
	protected PageFactory settingsPageFactory;

	@Resource(name = "registrationPageFactory")
	protected PageFactory registrationPageFactory;

	public AbstractGrassPage(GrassRequest request) {
		super("grass", request);
		addStyleName("grasspage");

		init();
	}

	protected void init() {

		// homelink (přes logo)
		Link homelink = new Link();
		homelink.addStyleName("homelink");
		homelink.setResource(getPageResource(homePageFactory));
		homelink.setIcon(new ThemeResource("img/logo.png"));
		addComponent(homelink, "homelink");

		// hlášky
		createQuotes(this);

		// menu
		HorizontalLayout menuExpander = new HorizontalLayout();
		menuExpander.setWidth("970px");
		// menuExpander.setHeight("70px");
		menuExpander.addStyleName("menu");
		menuExpander.setMargin(false);
		menuExpander.setSpacing(false);
		addComponent(menuExpander, "menu");

		HorizontalLayout menu = new HorizontalLayout();
		menu.setSpacing(true);
		// menu.addStyleName("v-menubar");
		menu.setWidth("100%");
		menuExpander.addComponent(menu);

		createMenuItems(menu);

		// obsah
		createContent(this);

		// footer
		addComponent(new Label("Powered by GRASS III © 2012-2017 Hynek Uhlíř"), "about");

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
			// nastavení
			createMenuComponent(menu, new Link(userInfoDTO.getName(), getPageResource(settingsPageFactory)));

			// odhlásit
			createMenuComponent(menu, new Link("Odhlásit", getPageResource(logoutPageFactory)));
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

	public GrassLayout getContent() {
		return this;
	}

}

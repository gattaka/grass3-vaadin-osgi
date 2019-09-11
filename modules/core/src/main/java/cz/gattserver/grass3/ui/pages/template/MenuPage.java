package cz.gattserver.grass3.ui.pages.template;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinRequest;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.modules.register.ModuleRegister;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.services.VersionInfoService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.util.UIUtils;

public abstract class MenuPage extends GrassPage {

	private static final long serialVersionUID = 8095742933880807949L;

	@Autowired
	protected VersionInfoService versionInfoService;

	@Lazy
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
	protected Div createPayload() {
		Div payload = new Div();

		Div holder = new Div();
		holder.setId("holder");
		payload.add(holder);

		Div topHolder = new Div();
		topHolder.setId("top-holder");
		holder.add(topHolder);

		Div top = new Div();
		top.setId("top");
		topHolder.add(top);

		// homelink (přes logo)
		String url = VaadinRequest.getCurrent().getContextPath();
		Anchor homelink = new Anchor(url, new Image("img/logo.png", "Gattserver"));
		homelink.setId("homelink");
		top.add(homelink);

		Div quotes = new Div();
		quotes.setId("quotes");
		top.add(quotes);

		createQuotes(quotes);

		Div menu = new Div();
		menu.setId("menu-wrapper");
		top.add(menu);

		HorizontalLayout menuExpander = new HorizontalLayout();
		menuExpander.setWidth("990px");
		menuExpander.addClassName("menu");
		menuExpander.setPadding(false);
		menuExpander.setSpacing(true);
		menu.add(menuExpander);

		createMenuItems(menuExpander);

		Div content = new Div();
		content.setId("content");
		holder.add(content);

		createContent(content);

		Div bottomHolder = new Div();
		bottomHolder.setId("bottom-holder");
		payload.add(bottomHolder);

		Div bottom = new Div();
		bottom.setId("bottom");
		bottomHolder.add(bottom);

		bottom.add(new Span("Powered by GRASS " + versionInfoService.getProjectVersion() + " © 2012-2019 Hynek Uhlíř"));

		Div bottomShadow = new Div();
		bottomShadow.setId("bottomshadow");
		bottomHolder.add(bottomShadow);

		return payload;
	}

	protected void createMenuComponent(HorizontalLayout menu, Anchor component) {
		menu.add(component);
		component.addClassName("menu-item");
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
					new Anchor(getPageURL(nodePageFactory, node.getId() + "-" + node.getName()), node.getName()));
		}

		// externí sekce
		for (SectionService section : serviceHolder.getSectionServices()) {
			if (coreACL.canShowSection(section, UIUtils.getUser())) {
				createMenuComponent(menu,
						new Anchor(getPageURL(section.getSectionPageFactory()), section.getSectionCaption()));
			}
		}

		Label sep = new Label(" ");
		menu.add(sep);
		menu.expand(sep);

		/**
		 * User menu
		 */

		// Přihlášení
		if (!coreACL.isLoggedIn(UIUtils.getUser()))
			createMenuComponent(menu, new Anchor(getPageURL(loginPageFactory), "Přihlášení"));

		// Registrace
		if (coreACL.canRegistrate(UIUtils.getUser()))
			createMenuComponent(menu, new Anchor(getPageURL(registrationPageFactory), "Registrace"));

		// Přehled o uživateli
		final UserInfoTO userInfoDTO = UIUtils.getUser();
		if (coreACL.canShowUserDetails(userInfoDTO, UIUtils.getUser())) {
			// nastavení
			createMenuComponent(menu, new Anchor(getPageURL(settingsPageFactory), "Nastavení"));

			// odhlásit
			createMenuComponent(menu, new Anchor(getPageURL("logout"), "Odhlásit (" + userInfoDTO.getName() + ")"));
		}
	}

	/**
	 * Získá hlášky
	 * 
	 * @param layout
	 *            layout, do kterého bude vytvořen obsah
	 */
	protected abstract void createQuotes(Div layout);

	/**
	 * Získá obsah
	 * 
	 * @param layout
	 *            layout, do kterého bude vytvořen obsah
	 */
	protected abstract void createContent(Div layout);

}

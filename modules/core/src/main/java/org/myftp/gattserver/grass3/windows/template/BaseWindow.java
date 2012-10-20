package org.myftp.gattserver.grass3.windows.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.model.dao.QuoteDAO;
import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.windows.HomeWindow;
import org.myftp.gattserver.grass3.windows.LoginWindow;
import org.myftp.gattserver.grass3.windows.QuotesWindow;
import org.myftp.gattserver.grass3.windows.RegistrationWindow;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public abstract class BaseWindow extends BackgroundWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private HorizontalLayout sectionsMenuLayout = new HorizontalLayout();
	private Link quotes;

	@Override
	protected void onShow() {

		// update menu sekcí
		updateMenu();

		// update hlášek
		quotes.setCaption(chooseQuote());
	}

	private void updateMenu() {
		sectionsMenuLayout.removeAllComponents();
		createHomeLink();
		for (ISectionService section : ServiceHolder.getInstance()
				.getSectionServices()) {
			createSectionLink(section.getSectionCaption(),
					section.getSectionWindowClass());
		}
	}

	private String chooseQuote() {
		QuoteDAO quoteDAO = new QuoteDAO();
		Long count = quoteDAO.count();
		if (count == null) {
			showError500();
		}
		Quote quotes = null;
		if (count != 0) {
			Random generator = new Random();
			Long randomId = Math.abs(generator.nextLong()) % count + 1;
			quotes = quoteDAO.findByID(randomId);
		}
		return quotes == null ? "~ nebyly nalezeny žádné záznamy ~" : quotes
				.getName();
	}

	protected void buildLayout(VerticalLayout layout) {

		// vytvoří tělo, které je sesypané směrem nahoru
		createBody(layout);

		// vytvoření patičku, která je sesypána dolů
		createFooter(layout);

	}

	private void createBody(VerticalLayout layout) {

		AbsoluteLayout bodyLayout = new AbsoluteLayout();
		layout.addComponent(bodyLayout);
		layout.setComponentAlignment(bodyLayout, Alignment.TOP_CENTER);

		// DŮLEŽITÉ - bez tohohle bude footer užírat svým top padding skoro
		// půlku stránky, čímž "ukousne" i existující část body-části
		layout.setExpandRatio(bodyLayout, 1.0f);

		bodyLayout.setStyleName("body_layout");
		bodyLayout.setWidth("990px");
		bodyLayout.setHeight("100%");

		createTop(bodyLayout);
		createMenu(bodyLayout);

		// obsah stránky
		createWindowContent(bodyLayout);

	}

	private void createFooter(VerticalLayout layout) {
		HorizontalLayout footerLayout = new HorizontalLayout();
		layout.addComponent(footerLayout);
		// layout.setComponentAlignment(footerLayout, Alignment.BOTTOM_CENTER);

		footerLayout.setWidth("100%");
		footerLayout.setHeight("25px");
		footerLayout.setStyleName("footer_layout");

		Label footerNote = new Label("GRASS3 Copyright Hynek Uhlíř 2012");
		footerLayout.addComponent(footerNote);

		HorizontalLayout footerShadow = new HorizontalLayout();
		layout.addComponent(footerShadow);
		layout.setComponentAlignment(footerShadow, Alignment.BOTTOM_CENTER);
		footerShadow.setStyleName("footer_shadow");
		footerShadow.setWidth("100%");
		footerShadow.setHeight("11px");
	}

	private void createTop(AbsoluteLayout layout) {

		HorizontalLayout topLayout = new HorizontalLayout();
		layout.addComponent(topLayout, "top: 0px; left: 0px");
		topLayout.setWidth("990px");
		topLayout.setHeight("94px");

		// logo (image)
		Embedded logoImage = new Embedded("", new ThemeResource("img/logo.png"));
		topLayout.addComponent(logoImage);
		logoImage.setAlternateText("Gattserver");
		logoImage.setStyleName("logo_image");

		// Hlášky - generují se znova a znova
		quotes = new Link(chooseQuote(), getWindowResource(QuotesWindow.class));
		quotes.setStyleName("quotes");
		quotes.setWidth("740px");
		topLayout.addComponent(quotes);
	}

	private void createMenu(AbsoluteLayout layout) {

		// menu (centrovací element)
		HorizontalLayout menuHolderLayout = new HorizontalLayout();
		layout.addComponent(menuHolderLayout, "top: 81px; left: 0px");
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

	private void createUserMenu(HorizontalLayout layout) {
		HorizontalLayout userMenuLayout = new HorizontalLayout();
		layout.addComponent(userMenuLayout);
		layout.setComponentAlignment(userMenuLayout, Alignment.MIDDLE_RIGHT);
		userMenuLayout.setStyleName("user_menu_layout");

		// Přihlašování
		Link link = new Link("Přihlášení", getWindowResource(LoginWindow.class));
		link.setStyleName("menu_item");
		userMenuLayout.addComponent(link);

		// Nastavení
		link = new Link("Nastavení", getWindowResource(SettingsWindow.class));
		link.setStyleName("menu_item");
		userMenuLayout.addComponent(link);

		// Registrovat
		// TODO - povolit dle konfigurace
		link = new Link("Registrace", getWindowResource(RegistrationWindow.class));
		link.setStyleName("menu_item");
		userMenuLayout.addComponent(link);

	}

	/**
	 * Vytvoří obsah okna - tedy všechno to, co je mezi menu a footerem
	 * 
	 * @param layout
	 *            layout, do kterého se má vytvářet
	 */
	protected abstract void createWindowContent(AbsoluteLayout layout);

}

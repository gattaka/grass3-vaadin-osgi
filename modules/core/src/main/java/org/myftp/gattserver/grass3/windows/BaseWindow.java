package org.myftp.gattserver.grass3.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.myftp.gattserver.grass3.data.SectionFacade;
import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.util.URLTool;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public abstract class BaseWindow extends Window {

	private static final long serialVersionUID = 2474374292329895766L;

	// Fasády
	private SectionFacade sectionFacade = SectionFacade.getInstance();
	private QuotesFacade quotesFacade = QuotesFacade.getInstance();

	public BaseWindow() {

		// Hlavní layout - nosič pozadí a rovnoměrného rozsazení elementů
		VerticalLayout backgroundLayout = new VerticalLayout();
		setContent(backgroundLayout);
		backgroundLayout.setStyleName("background_layout");
		backgroundLayout.setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		backgroundLayout.addComponent(layout);
		layout.setStyleName("layout");
		layout.setSizeFull();

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

		// Hlášky - TODO .. generovat znova a znova :)
		int count = quotesFacade.getQuotesCount();
		QuoteDTO quotesDTO = null;
		if (count != 0) {
			Random generator = new Random();
			Long randomId = Math.abs(generator.nextLong()) % count + 1;
			quotesDTO = quotesFacade.getByID(randomId);
		}
		String quote = quotesDTO == null ? "" : quotesDTO.getName();

		Link quotes = new Link(quote, new ExternalResource(
				URLTool.getWindowURL(QuotesWindow.NAME)));
		topLayout.addComponent(quotes);
		quotes.setStyleName("quotes");
		quotes.setWidth("740px");

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

	private void createSectionsMenu(HorizontalLayout layout) {
		HorizontalLayout sectionsMenuLayout = new HorizontalLayout();
		layout.addComponent(sectionsMenuLayout);
		layout.setComponentAlignment(sectionsMenuLayout, Alignment.MIDDLE_LEFT);
		sectionsMenuLayout.setStyleName("sections_menu_layout");

		// Přihlašování
		Link link = new Link("Domů", new ExternalResource(
				URLTool.getWindowURL(HomeWindow.NAME)));
		link.setStyleName("first_menu_item");
		sectionsMenuLayout.addComponent(link);

		for (SectionFacade.Section section : sectionFacade.getSections()) {
			Label menuItem = new Label(section.getName());
			sectionsMenuLayout.addComponent(menuItem);

			menuItem.setStyleName("menu_item");
		}
	}

	private void createUserMenu(HorizontalLayout layout) {
		HorizontalLayout userMenuLayout = new HorizontalLayout();
		layout.addComponent(userMenuLayout);
		layout.setComponentAlignment(userMenuLayout, Alignment.MIDDLE_RIGHT);
		userMenuLayout.setStyleName("user_menu_layout");

		List<String> options = new ArrayList<String>(2);
		options.add("Registrovat");

		// Přihlašování
		Link link = new Link("Přihlášení", new ExternalResource(
				URLTool.getWindowURL(LoginWindow.NAME)));
		link.setStyleName("menu_item");
		userMenuLayout.addComponent(link);

		for (String option : options) {
			Label userItem = new Label(option);
			userMenuLayout.addComponent(userItem);
			userItem.setStyleName("menu_item");
		}
	}

	/**
	 * Vytvoří obsah okna - tedy všechno to, co je mezi menu a footerem
	 * 
	 * @param layout
	 *            layout, do kterého se má vytvářet
	 */
	protected abstract void createWindowContent(AbsoluteLayout layout);

}

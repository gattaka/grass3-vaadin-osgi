package org.myftp.gattserver.grass3.pages.template;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public abstract class GrassPage extends GrassLayout {

	private static final long serialVersionUID = 604170960797872356L;

	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;

	public GrassPage(GrassRequest request) {
		super("grass", request);
		addStyleName("grasspage");
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		// homelink (přes logo)
		Link homelink = new Link();
		homelink.addStyleName("homelink");
		homelink.setResource(getPageResource(homePageFactory));
		homelink.setIcon(new ThemeResource("img/logo.png"));
		addComponent(homelink, "homelink");

		// hlášky
		createQuotes(this);

		// menu
		createMenu(this);

		// obsah
		createContent(this);

		// footer
		addComponent(new Label("GRASS3"), "about");
	}

	/**
	 * Získá hlášky
	 */
	protected abstract void createQuotes(CustomLayout layout);

	/**
	 * Získá menu
	 */
	protected abstract void createMenu(CustomLayout layout);

	/**
	 * Získá obsah
	 */
	protected abstract void createContent(CustomLayout layout);

}

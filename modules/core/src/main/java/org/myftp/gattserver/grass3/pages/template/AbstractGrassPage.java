package org.myftp.gattserver.grass3.pages.template;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.myftp.gattserver.grass3.js.GrassJSBootstrapComponent;
import org.myftp.gattserver.grass3.js.JQueryBootstrapComponent;
import org.myftp.gattserver.grass3.js.JQueryUIBootstrapComponent;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public abstract class AbstractGrassPage extends GrassLayout implements
		IGrassPage {

	private static final long serialVersionUID = 604170960797872356L;

	@Resource(name = "homePageFactory")
	private IPageFactory homePageFactory;

	private Set<String> initJS = new LinkedHashSet<String>();

	public AbstractGrassPage(GrassRequest request) {
		super("grass", request);
		addStyleName("grasspage");
	}

	@PostConstruct
	protected void init() {

		// nejprve nahraj potřebné knihovny
		addComponent(new JQueryBootstrapComponent());
//		addComponent(new JQueryUIBootstrapComponent()); // tahle by se nemusela nahrávat vždy
//		addComponent(new GrassJSBootstrapComponent());
	
		submitInitJS(initJS);
		gatherInitJS();

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
	 * Přihlásí skripty
	 * 
	 * @param initJS
	 */
	protected void submitInitJS(Set<String> initJS) {
		// initJS.add(getRequest().getContextRoot()
		// + "/VAADIN/themes/grass/js/grass.js");
	}

	/**
	 * Vezme všechen nahlášený JS init obsah a provede ho (kaskádově s ohledem
	 * na závislosti)
	 */
	private void gatherInitJS() {

		StringBuilder loadScript = new StringBuilder();

		// nejprve jQuery
		// loadScript
		// .append("var head= document.getElementsByTagName('head')[0];")
		// .append("var script= document.createElement('script');")
		// .append("script.type= 'text/javascript';")
		// .append("script.src= '").append(getRequest().getContextRoot())
		// .append("/VAADIN/themes/grass/js/jquery.js';")
		// .append("var callback = function() {");

		// ostatní JS už lze nahrávat pomocí jQuery
		for (String js : initJS) {
			loadScript.append("$.getScript('" + js + "', function(){});");
		}

		// // uzavřít
		// for (int i = 0; i < initJS.size(); i++) {
		// loadScript.append("});");
		// }
		//
		// // konec jQuery
		// loadScript.append("};").append("script.onreadystatechange = callback;")
		// .append("script.onload = callback;")
		// .append("head.appendChild(script);");

		// fire !
		JavaScript.getCurrent().execute(loadScript.toString());

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

	public GrassLayout getContent() {
		return this;
	}

}

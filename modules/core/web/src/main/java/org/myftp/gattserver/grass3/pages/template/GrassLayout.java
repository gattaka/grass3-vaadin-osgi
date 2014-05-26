package org.myftp.gattserver.grass3.pages.template;

import org.myftp.gattserver.grass3.GrassUI;
import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.js.JQueryBootstrapComponent;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.subwindows.ErrorWindow;
import org.myftp.gattserver.grass3.subwindows.InfoWindow;
import org.myftp.gattserver.grass3.subwindows.WarnWindow;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;

public abstract class GrassLayout extends CustomLayout {

	private static final long serialVersionUID = 604170960797872356L;

	private GrassRequest request;

	public GrassLayout(String layoutName, GrassRequest request) {
		super(layoutName);
		this.request = request;
		SpringContextHelper.inject(this);
	}

	protected GrassRequest getRequest() {
		return request;
	}

	/**
	 * Nahraje CSS
	 * 
	 * @param link
	 *            odkaz k css souboru - relativní, absolutní (http://...)
	 */
	public void loadCSS(String link) {
		StringBuilder loadStylesheet = new StringBuilder();
		loadStylesheet.append("var head= document.getElementsByTagName('head')[0];")
				.append("var link= document.createElement('link');").append("link.type= 'text/css';")
				.append("link.rel= 'stylesheet';").append("link.href= '" + link + "';")
				.append("head.appendChild(link);");
		JavaScript.eval(loadStylesheet.toString());
	}

	/**
	 * Nahraje více JS skriptů, synchronně za sebou (mohou se tedy navzájem na
	 * sebe odkazovat a bude zaručeno, že 1. skript bude celý nahrán před 2.
	 * skriptem, který využívá jeho funkcí)
	 * 
	 * @param links
	 */
	public void loadJS(JScriptItem... scripts) {
		if (request.isjQueryPresent() == false) {
			addComponent(new JQueryBootstrapComponent());
			request.setjQueryPresent(true);
		}
		StringBuilder builder = new StringBuilder();
		buildJSBatch(builder, 0, scripts);
		JavaScript.eval(builder.toString());
	}

	// public void loadJS(JScriptItem... scripts) {
	// StringBuilder builder = new StringBuilder();
	// buildJSBatch(builder, 0, scripts);
	//
	// StringBuilder jQueryBuilder = new StringBuilder();
	// jQueryBuilder
	// .append("if (true) {var head= document.getElementsByTagName('head')[0]; "
	// + "var script= document.createElement('script'); " +
	// "script.type= 'text/javascript'; "
	// + "script.src= './VAADIN/themes/grass/js/jquery.js'; "
	// + "script.onreadystatechange= function () {" +
	// "if (this.readyState == 'complete') { alert($);")
	// .append(builder).append("}};" + "head.appendChild(script); }");
	// JavaScript.eval(jQueryBuilder.toString());
	// }

	private void buildJSBatch(StringBuilder builder, int index, JScriptItem... scripts) {
		if (index >= scripts.length)
			return;

		JScriptItem js = scripts[index];
		String chunk = js.getScript();
		if (js.isPlain() == false) {
			// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
			// obejít problém se závislosí pluginů na úložišti theme apod. a
			// přitom umožnit aby se JS odkazovali na externí zdroje
			if (!chunk.toLowerCase().startsWith("http://"))
				chunk = "\"" + getRequest().getContextRoot() + "/VAADIN/themes/grass/" + chunk + "\"";
		}

		builder.append("$.getScript(").append(chunk).append(", function(){");
		buildJSBatch(builder, index + 1, scripts);
		builder.append("});");
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix dle pageFactory
	 */
	public String getPageURL(IPageFactory pageFactory) {
		return request.getContextRoot() + "/" + pageFactory.getPageName();
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix
	 */
	public String getPageURL(String suffix) {
		return request.getContextRoot() + "/" + suffix;
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix dle pageFactory + relativní URL
	 */
	public String getPageURL(IPageFactory pageFactory, String... relativeURLs) {
		if (relativeURLs.length == 1) {
			return getPageURL(pageFactory) + "/" + relativeURLs[0];
		} else {
			StringBuffer buffer = new StringBuffer();
			buffer.append(getPageURL(pageFactory));
			for (String relativeURL : relativeURLs) {
				if (relativeURL != null) {
					buffer.append("/");
					buffer.append(relativeURL);
				}
			}
			return buffer.toString();
		}
	}

	/**
	 * Získá resource dle stránky
	 */
	public ExternalResource getPageResource(IPageFactory pageFactory) {
		return new ExternalResource(getPageURL(pageFactory));
	}

	/**
	 * Získá resource dle stránky + relativní URL
	 */
	public ExternalResource getPageResource(IPageFactory pageFactory, String... relativeURLs) {
		return new ExternalResource(getPageURL(pageFactory, relativeURLs));
	}

	/**
	 * Získá uživatele
	 */
	public UserInfoDTO getUser() {
		return getGrassUI().getUser();
	}

	/**
	 * Získá aktuální UI jako {@link GrassUI}
	 */
	public GrassUI getGrassUI() {
		return (GrassUI) UI.getCurrent();
	}

	/**
	 * Přejde na stránku
	 */
	public void redirect(String uri) {
		Page.getCurrent().setLocation(uri);
	}

	/**
	 * Notifikace pomocí {@link InfoWindow}
	 */
	public void showInfo(String caption) {
		InfoWindow infoSubwindow = new InfoWindow(caption);
		getGrassUI().addWindow(infoSubwindow);
	}

	/**
	 * Notifikace varování pomocí {@link WarnWindow}
	 */
	public void showWarning(String caption) {
		WarnWindow warnSubwindow = new WarnWindow(caption);
		getGrassUI().addWindow(warnSubwindow);
	}

	/**
	 * Notifikace chyby pomocí {@link ErrorWindow}
	 */
	public void showError(String caption) {
		ErrorWindow errorSubwindow = new ErrorWindow(caption);
		getGrassUI().addWindow(errorSubwindow);
	}

	/**
	 * Vyhodí chybu
	 */
	public void showError500() {
		request.setError500();
	}

	/**
	 * Vyhodí chybu
	 */
	public void showError404() {
		request.setError404();
	}

	/**
	 * Vyhodí chybu
	 */
	public void showError403() {
		request.setError403();
	}

}

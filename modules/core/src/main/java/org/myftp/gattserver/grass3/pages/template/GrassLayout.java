package org.myftp.gattserver.grass3.pages.template;

import org.myftp.gattserver.grass3.GrassUI;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.subwindows.WarnSubwindow;
import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.UI;

public abstract class GrassLayout extends CustomLayout {

	private static final long serialVersionUID = 604170960797872356L;

	private GrassRequest request;

	public GrassLayout(String layoutName, GrassRequest request) {
		super(layoutName);
		this.request = request;
	}

	protected GrassRequest getRequest() {
		return request;
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
	public ExternalResource getPageResource(IPageFactory pageFactory,
			String... relativeURLs) {
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
	 * Notifikace pomocí {@link InfoSubwindow}
	 */
	public void showInfo(String caption) {
		InfoSubwindow infoSubwindow = new InfoSubwindow(caption);
		getGrassUI().addWindow(infoSubwindow);
	}

	/**
	 * Notifikace varování pomocí {@link WarnSubwindow}
	 */
	public void showWarning(String caption) {
		WarnSubwindow warnSubwindow = new WarnSubwindow(caption);
		getGrassUI().addWindow(warnSubwindow);
	}

	/**
	 * Notifikace chyby pomocí {@link ErrorSubwindow}
	 */
	public void showError(String caption) {
		ErrorSubwindow errorSubwindow = new ErrorSubwindow(caption);
		getGrassUI().addWindow(errorSubwindow);
	}

	/**
	 * Vyhodí chybu
	 */
	public void showError500() {
		redirect(getPageURL("err500"));
	}

	/**
	 * Vyhodí chybu
	 */
	public void showError404() {
		redirect(getPageURL("err404"));
	}

	/**
	 * Vyhodí chybu
	 */
	public void showError403() {
		redirect(getPageURL("err403"));
	}

}

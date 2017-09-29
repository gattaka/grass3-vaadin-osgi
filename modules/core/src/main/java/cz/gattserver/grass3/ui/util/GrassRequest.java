package cz.gattserver.grass3.ui.util;

import com.vaadin.server.VaadinRequest;

import cz.gattserver.grass3.PageState;
import cz.gattserver.web.common.URLPathAnalyzer;

/**
 * Třída poskytující veškeré informace o requestu od Vaadinu, upravené tak, aby
 * šli snadno používat v objektech stránek Grassu
 * 
 * @author gatt
 * 
 */
public class GrassRequest {

	private VaadinRequest vaadinRequest;
	private URLPathAnalyzer analyzer;
	private String contextRoot;

	/**
	 * Byl již nahrán jQuery skript ? Je nahráván lazy, aby se urychlilo
	 * nahrávání stránek
	 */
	private boolean jQueryPresent = false;

	/**
	 * Stav stránky
	 */
	private PageState pageState = PageState.CLEAN;

	public GrassRequest(VaadinRequest vaadinRequest) {
		this.vaadinRequest = vaadinRequest;
		this.analyzer = new URLPathAnalyzer(vaadinRequest.getPathInfo());
		this.contextRoot = vaadinRequest.getContextPath();
	}

	public String getContextRoot() {
		return contextRoot;
	}

	public VaadinRequest getVaadinRequest() {
		return vaadinRequest;
	}

	public URLPathAnalyzer getAnalyzer() {
		return analyzer;
	}

	public boolean isjQueryPresent() {
		return jQueryPresent;
	}

	public void setjQueryPresent(boolean jQueryPresent) {
		this.jQueryPresent = jQueryPresent;
	}

	public PageState getPageState() {
		return pageState;
	}

	/**
	 * Nastavuje stav stránky - lze nastavit pouze pokud je čistá, pokud je už u
	 * stránky vedena nějaká chyba, další chyby jí nepřepisují, protože mohou
	 * být důsledky první chyby
	 */
	private void setPageState(PageState newState) {
		if (pageState == PageState.CLEAN)
			pageState = newState;
	}

	/**
	 * Vyhodí chybu
	 */
	public void setError500() {
		setPageState(PageState.E500);
	}

	/**
	 * Vyhodí chybu
	 */
	public void setError404() {
		setPageState(PageState.E404);
	}

	/**
	 * Vyhodí chybu
	 */
	public void setError403() {
		setPageState(PageState.E403);
	}

}

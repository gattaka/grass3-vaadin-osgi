package cz.gattserver.grass3.ui.util;

import com.vaadin.server.VaadinRequest;

import cz.gattserver.web.common.URLPathAnalyzer;

/**
 * Třída poskytující veškeré informace o requestu od Vaadinu, upravené tak, aby
 * šli snadno používat v objektech stránek Grassu.
 * 
 * @author gatt
 * 
 * @see VaadinRequest
 * @see URLPathAnalyzer
 * 
 */
public class GrassRequest {

	private final VaadinRequest vaadinRequest;
	private final URLPathAnalyzer analyzer;
	private final String contextRoot;

	public GrassRequest(VaadinRequest vaadinRequest) {
		this.vaadinRequest = vaadinRequest;
		this.analyzer = new URLPathAnalyzer(vaadinRequest.getPathInfo());
		this.contextRoot = vaadinRequest.getContextPath();
	}

	/**
	 * Získá contextRoot stránky, tedy první položku v URI.
	 * 
	 * @return část URI obsahující contextRoot
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * Získá původní {@link VaadinRequest}
	 * 
	 * @return vaadinRequest
	 */
	public VaadinRequest getVaadinRequest() {
		return vaadinRequest;
	}

	/**
	 * Získá {@link URLPathAnalyzer}, který Grass používá pro parsování cest ke
	 * stránkám
	 * 
	 * @return {@link URLPathAnalyzer} ve stavu dle aktuálního requestu
	 */
	public URLPathAnalyzer getAnalyzer() {
		return analyzer;
	}

}

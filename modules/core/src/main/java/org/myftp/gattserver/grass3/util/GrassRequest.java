package org.myftp.gattserver.grass3.util;

import com.vaadin.server.VaadinRequest;

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

	public GrassRequest(VaadinRequest vaadinRequest) {
		this.vaadinRequest = vaadinRequest;
		this.analyzer = new URLPathAnalyzer(vaadinRequest.getPathInfo());
	}

	public VaadinRequest getVaadinRequest() {
		return vaadinRequest;
	}

	public URLPathAnalyzer getAnalyzer() {
		return analyzer;
	}

}

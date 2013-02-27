package org.myftp.gattserver.grass3.windows.template;

import java.util.Collections;
import java.util.Map;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.PageFactoriesMap;

public abstract class PageFactory {

	private String pageName;

	private static PageFactoriesMap factories = new PageFactoriesMap();

	public PageFactory(String pageName) {
		this.pageName = pageName;
		factories.put(this);
	}

	public String getPageName() {
		return pageName;
	}

	public abstract GrassPage createPage(GrassRequest request);

	public static void setHomepageFactory(PageFactory homepageFactory) {
		factories.setHomepageFactory(homepageFactory);
	}

	public static Map<String, PageFactory> getRegistredFactories() {
		return Collections.unmodifiableMap(factories);
	}
}

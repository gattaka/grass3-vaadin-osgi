package org.myftp.gattserver.grass3.windows.ifces;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.GrassPage;

public abstract class PageFactory {

	private String pageName;

	public PageFactory(String pageName) {
		this.pageName = pageName;
	}

	public String getPageName() {
		return pageName;
	}

	public abstract GrassPage createPage(GrassRequest request);

}

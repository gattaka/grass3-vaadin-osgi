package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class PageFactory implements ApplicationContextAware {

	private String pageName;

	protected ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public PageFactory(String pageName) {
		this.pageName = pageName;
	}

	public String getPageName() {
		return pageName;
	}

	public abstract GrassPage createPage(GrassRequest request);

}

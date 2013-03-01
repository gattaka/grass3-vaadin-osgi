package org.myftp.gattserver.grass3.pages.factories.template;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.template.GrassPage;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class PageFactory implements ApplicationContextAware {

	private String pageName;
	private String beanName;

	protected ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Konstruktor
	 * 
	 * @param pageName
	 *            jméno stránky (URL, dle kterého se k ní bude přistupovat)
	 * @param beanName
	 *            jméno spring bean-y stránky
	 */
	public PageFactory(String pageName, String beanName) {
		this.pageName = pageName;
		this.beanName = beanName;
	}

	public String getPageName() {
		return pageName;
	}

	public GrassPage createPage(GrassRequest request) {
		return (GrassPage) applicationContext.getBean(beanName, request);
	}
	
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.USER);
	}

}

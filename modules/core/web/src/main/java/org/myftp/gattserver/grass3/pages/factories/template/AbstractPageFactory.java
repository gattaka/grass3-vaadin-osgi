package org.myftp.gattserver.grass3.pages.factories.template;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.ISecurityFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractPageFactory implements IPageFactory, ApplicationContextAware {
	private static final long serialVersionUID = 3988625640870100368L;

	private String pageName;
	private String beanName;

	protected ApplicationContext applicationContext;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
	public AbstractPageFactory(String pageName, String beanName) {
		this.pageName = pageName;
		this.beanName = beanName;
	}

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoDTO} objekt
	 */
	protected UserInfoDTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public String getPageName() {
		return pageName;
	}

	protected abstract boolean isAuthorized();

	public GrassLayout createPage(GrassRequest request) {
		return ((IGrassPage) applicationContext.getBean(isAuthorized() ? beanName : "err403", request)).getContent();
	}

}

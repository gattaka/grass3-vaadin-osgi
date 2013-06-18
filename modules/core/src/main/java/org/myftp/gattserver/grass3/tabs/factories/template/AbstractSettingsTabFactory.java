package org.myftp.gattserver.grass3.tabs.factories.template;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.ISecurityFacade;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.tabs.template.ISettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractSettingsTabFactory implements
		ISettingsTabFactory, ApplicationContextAware {

	private String tabName;
	private String tabURL;
	private String beanName;

	protected ApplicationContext applicationContext;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Konstruktor
	 * 
	 * @param name
	 *            název karty nastavení, který bude zobrazen v levém menu - měl
	 *            by začínat velkým písmenem
	 * @param tabURL
	 *            URL část, přes kterou se bude dát na settings kartu dostat,
	 *            měla by to být verze name bez diakritiky a obecně pouze s
	 *            URL-friendly znaky
	 * @param beanName
	 *            jméno spring bean-y karty nastavení
	 */
	public AbstractSettingsTabFactory(String name, String tabURL,
			String beanName) {
		this.tabName = name;
		this.beanName = beanName;
		this.tabURL = tabURL;
	}

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoDTO} objekt
	 */
	protected UserInfoDTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public String getSettingsCaption() {
		return tabName;
	}

	public String getSettingsURL() {
		return tabURL;
	}

	public GrassLayout createPage(GrassRequest request) {
		return ((ISettingsTab) applicationContext.getBean(beanName, request))
				.getContent();
	}

}

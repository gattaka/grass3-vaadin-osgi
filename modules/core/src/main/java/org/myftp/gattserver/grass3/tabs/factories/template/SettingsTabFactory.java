package org.myftp.gattserver.grass3.tabs.factories.template;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.template.SettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class SettingsTabFactory implements ApplicationContextAware {

	private String tabName;
	private String tabURL;
	private String beanName;

	protected ApplicationContext applicationContext;

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
	public SettingsTabFactory(String name, String tabURL, String beanName) {
		this.tabName = name;
		this.beanName = beanName;
		this.tabURL = tabURL;
	}

	public String getSettingsCaption() {
		return tabName;
	}

	public String getSettingsURL() {
		return tabURL;
	}

	public SettingsTab createPage(GrassRequest request) {
		return (SettingsTab) applicationContext.getBean(beanName, request);
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.USER);
	}

}

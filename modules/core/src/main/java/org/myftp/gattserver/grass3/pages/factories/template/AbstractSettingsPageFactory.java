package org.myftp.gattserver.grass3.pages.factories.template;

public abstract class AbstractSettingsPageFactory extends PageFactory {

	/**
	 * Konstruktor
	 * 
	 * @param pageName
	 *            bez prefixu "settings/", bude sám přidán
	 * @param beanName
	 *            jméno spring bean-y stránky
	 */
	public AbstractSettingsPageFactory(String pageName, String beanName) {
		super("settings/" + pageName, beanName);
	}

	public abstract String getSettingsCaption();

}

package cz.gattserver.grass3.ui.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

public abstract class AbstractModuleSettingsPageFactory implements ModuleSettingsPageFactory {

	private String tabName;
	private String tabURL;

	@Autowired
	private SecurityService securityFacade;

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
	 */
	public AbstractModuleSettingsPageFactory(String name, String tabURL) {
		this.tabName = name;
		this.tabURL = tabURL;
	}

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoTO} objekt
	 */
	protected UserInfoTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public String getSettingsCaption() {
		return tabName;
	}

	public String getSettingsURL() {
		return tabURL;
	}

	public GrassPage createPageIfAuthorized(GrassRequest request) {
		return createPage(request);
	}

	protected abstract GrassPage createPage(GrassRequest request);

}
package cz.gattserver.grass3.tabs.factories.template;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.facades.SecurityFacade;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.template.GrassLayout;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

public abstract class AbstractSettingsTabFactory implements SettingsTabFactory {

	private String tabName;
	private String tabURL;

	@Autowired
	private SecurityFacade securityFacade;

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
	public AbstractSettingsTabFactory(String name, String tabURL) {
		this.tabName = name;
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

	public GrassLayout createTabIfAuthorized(GrassRequest request) {
		return ((SettingsTab) createTab(request)).getContent();
	}

	protected abstract SettingsTab createTab(GrassRequest request);

}

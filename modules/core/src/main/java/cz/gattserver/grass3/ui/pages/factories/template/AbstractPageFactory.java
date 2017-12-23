package cz.gattserver.grass3.ui.pages.factories.template;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.pages.err.Error403Page;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

public abstract class AbstractPageFactory implements PageFactory {

	@Autowired
	private SecurityService securityFacade;

	private String pageName;

	protected abstract boolean isAuthorized();

	protected abstract GrassPage createPage(GrassRequest request);

	/**
	 * Konstruktor
	 * 
	 * @param pageName
	 *            jméno stránky (URL, dle kterého se k ní bude přistupovat)
	 */
	public AbstractPageFactory(String pageName) {
		this.pageName = pageName;
	}

	/**
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoTO} objekt
	 */
	protected UserInfoTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public String getPageName() {
		return pageName;
	}

	public GrassPage createPageIfAuthorized(GrassRequest request) {
		return isAuthorized() ? createPage(request) : new Error403Page(request);
	}

}

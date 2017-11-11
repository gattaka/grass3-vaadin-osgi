package cz.gattserver.grass3.pages.factories.template;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.facades.SecurityFacade;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.err.Error403Page;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

public abstract class AbstractPageFactory implements PageFactory {

	private static final long serialVersionUID = 3988625640870100368L;

	private String pageName;

	@Autowired
	private SecurityFacade securityFacade;

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
	 * Získá aktuálního přihlášeného uživatele jako {@link UserInfoDTO} objekt
	 */
	protected UserInfoDTO getUser() {
		return securityFacade.getCurrentUser();
	}

	public String getPageName() {
		return pageName;
	}

	protected abstract boolean isAuthorized();

	public GrassPage createPageIfAuthorized(GrassRequest request) {
		return isAuthorized() ? createPage(request) : new Error403Page(request);
	}

	protected abstract GrassPage createPage(GrassRequest request);

}

package cz.gattserver.grass3.pages.factories.template;

import javax.annotation.Resource;

import cz.gattserver.grass3.facades.ISecurityFacade;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pages.err.Err403Page;
import cz.gattserver.grass3.pages.template.GrassLayout;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public abstract class AbstractPageFactory implements IPageFactory {
	private static final long serialVersionUID = 3988625640870100368L;

	private String pageName;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

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

	public GrassLayout createPageIfAuthorized(GrassRequest request) {
		return (isAuthorized() ? createPage(request) : new Err403Page(request)).getContent();
	}

	protected abstract IGrassPage createPage(GrassRequest request);

}

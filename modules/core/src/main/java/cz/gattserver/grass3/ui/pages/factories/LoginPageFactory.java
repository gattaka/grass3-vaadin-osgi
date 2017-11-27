package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.ui.pages.LoginPage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("loginPageFactory")
public class LoginPageFactory extends AbstractPageFactory {

	@Autowired
	private CoreACLService coreACL;

	public LoginPageFactory() {
		// kvůli Spring security se tohle nesmí jmenovat login, musí tam být
		// něco jiného, aby se nechytil filtr -- ten rozhodí Vaadin JSON
		// komunikaci a stránka nenajede
		super("loginpage");
	}

	@Override
	protected boolean isAuthorized() {
		// lze pouze není-li přihlášen
		return !coreACL.isLoggedIn(getUser());
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new LoginPage(request);
	}

}

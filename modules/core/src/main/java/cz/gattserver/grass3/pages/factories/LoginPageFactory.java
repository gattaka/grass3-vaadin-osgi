package cz.gattserver.grass3.pages.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.LoginPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;

@Component("loginPageFactory")
public class LoginPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 259563676017857878L;

	@Autowired
	private CoreACL coreACL;

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

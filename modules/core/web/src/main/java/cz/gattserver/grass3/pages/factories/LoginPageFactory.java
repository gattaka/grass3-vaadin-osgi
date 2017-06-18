package cz.gattserver.grass3.pages.factories;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.LoginPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("loginPageFactory")
public class LoginPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 259563676017857878L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public LoginPageFactory() {
		super("login");
	}

	@Override
	protected boolean isAuthorized() {
		// lze pouze není-li přihlášen
		return !coreACL.isLoggedIn(getUser());
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new LoginPage(request);
	}

}

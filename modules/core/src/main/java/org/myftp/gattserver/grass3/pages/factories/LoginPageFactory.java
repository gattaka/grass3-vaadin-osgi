package org.myftp.gattserver.grass3.pages.factories;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.springframework.stereotype.Component;

@Component("loginPageFactory")
public class LoginPageFactory extends AbstractPageFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public LoginPageFactory() {
		super("login", "loginPage");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canLogin(getUser());
	}

}

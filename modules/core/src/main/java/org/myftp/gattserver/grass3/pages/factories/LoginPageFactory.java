package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("loginPageFactory")
public class LoginPageFactory extends AbstractPageFactory {

	public LoginPageFactory() {
		super("login", "loginPage");
	}
	
	@Override
	protected boolean isAuthorized() {
		return getUserACL().canLogin();
	}

}

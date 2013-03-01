package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("loginPageFactory")
public class LoginPageFactory extends PageFactory {

	public LoginPageFactory() {
		super("login", "loginPage");
	}

}

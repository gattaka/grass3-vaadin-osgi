package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends PageFactory {

	public RegistrationPageFactory() {
		super("registration", "registrationPage");
	}

}

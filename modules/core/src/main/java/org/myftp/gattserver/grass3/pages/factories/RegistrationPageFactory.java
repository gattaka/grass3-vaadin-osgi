package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends AbstractPageFactory {

	public RegistrationPageFactory() {
		super("registration", "registrationPage");
	}

}

package org.myftp.gattserver.grass3.pages.factories;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.springframework.stereotype.Component;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 3789569117229850461L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public RegistrationPageFactory() {
		super("registration", "registrationPage");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canRegistrate(getUser());
	}
}

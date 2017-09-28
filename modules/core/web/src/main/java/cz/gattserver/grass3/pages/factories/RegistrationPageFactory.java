package cz.gattserver.grass3.pages.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.RegistrationPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 3789569117229850461L;

	@Autowired
	private CoreACL coreACL;

	public RegistrationPageFactory() {
		super("registration");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canRegistrate(getUser());
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new RegistrationPage(request);
	}
}

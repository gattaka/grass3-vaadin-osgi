package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.ui.pages.RegistrationPage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 3789569117229850461L;

	@Autowired
	private CoreACLService coreACL;

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

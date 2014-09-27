package cz.gattserver.grass3.pages.factories;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.RegistrationPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 3789569117229850461L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public RegistrationPageFactory() {
		super("registration");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canRegistrate(getUser());
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new RegistrationPage(request);
	}
}

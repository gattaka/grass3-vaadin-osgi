package cz.gattserver.grass3.medic.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("medicPageFactory")
public class MedicPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 8984837128014801897L;

	public MedicPageFactory() {
		super("medic");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new MedicPage(request);
	}
}

package cz.gattserver.grass3.fm.web.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.fm.web.FMPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("fmPageFactory")
public class FMPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -2643365441921408821L;

	public FMPageFactory() {
		super("fm");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin() || getUser().hasRole(Role.FRIEND);
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new FMPage(request);
	}

}

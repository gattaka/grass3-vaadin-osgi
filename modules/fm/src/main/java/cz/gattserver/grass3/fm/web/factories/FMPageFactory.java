package cz.gattserver.grass3.fm.web.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.fm.web.FMPage;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("fmPageFactory")
public class FMPageFactory extends AbstractPageFactory {

	public FMPageFactory() {
		super("fm");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin() || getUser().hasRole(CoreRole.FRIEND);
	}

	@Override
	protected GrassPage createPage() {
		return new FMPage();
	}

}

package cz.gattserver.grass3.campgames.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.campgames.ui.pages.CampgamesPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("campgamesPageFactory")
public class CampgamesPageFactory extends AbstractPageFactory {

	public CampgamesPageFactory() {
		super("campgames");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new CampgamesPage(request);
	}
}

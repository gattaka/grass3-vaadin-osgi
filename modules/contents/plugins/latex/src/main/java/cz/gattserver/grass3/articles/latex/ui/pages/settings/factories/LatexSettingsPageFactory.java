package cz.gattserver.grass3.articles.latex.ui.pages.settings.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.latex.ui.pages.settings.LatexSettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component
public class LatexSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public LatexSettingsPageFactory() {
		super("LaTeX", "latex");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new LatexSettingsPage(request);
	}
}

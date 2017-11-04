package cz.gattserver.grass3.articles.latex.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.tabs.factories.template.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component
public class LatexSettingsTabFactory extends AbstractModuleSettingsPageFactory {

	public LatexSettingsTabFactory() {
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

package cz.gattserver.grass3.articles.latex.ui.pages.settings.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.latex.ui.pages.settings.LatexSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

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
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new LatexSettingsPageFragmentFactory();
	}
}

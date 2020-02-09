package cz.gattserver.grass3.articles.ui.pages.settings.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.ui.pages.settings.ArticlesSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

@Component
public class ArticlesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public ArticlesSettingsPageFactory() {
		super("Články", "articles");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new ArticlesSettingsPageFragmentFactory();
	}
}

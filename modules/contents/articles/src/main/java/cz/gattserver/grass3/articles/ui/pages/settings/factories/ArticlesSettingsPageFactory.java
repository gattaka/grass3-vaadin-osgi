package cz.gattserver.grass3.articles.ui.pages.settings.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.ui.pages.settings.ArticlesSettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component
public class ArticlesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public ArticlesSettingsPageFactory() {
		super("Články", "articles");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new ArticlesSettingsPage(request);
	}
}

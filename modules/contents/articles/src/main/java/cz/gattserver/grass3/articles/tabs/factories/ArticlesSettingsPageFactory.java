package cz.gattserver.grass3.articles.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.tabs.ArticlesSettingsPage;
import cz.gattserver.grass3.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

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

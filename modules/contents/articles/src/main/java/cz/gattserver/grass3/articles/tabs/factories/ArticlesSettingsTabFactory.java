package cz.gattserver.grass3.articles.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.tabs.ArticlesSettingsTab;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("articlesSettingsTabFactory")
public class ArticlesSettingsTabFactory extends AbstractSettingsTabFactory {

	public ArticlesSettingsTabFactory() {
		super("Články", "articles");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected SettingsTab createTab(GrassRequest request) {
		return new ArticlesSettingsTab(request);
	}
}

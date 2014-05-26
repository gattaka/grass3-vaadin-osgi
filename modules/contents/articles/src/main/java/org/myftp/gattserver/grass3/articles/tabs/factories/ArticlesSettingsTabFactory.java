package org.myftp.gattserver.grass3.articles.tabs.factories;

import org.myftp.gattserver.grass3.articles.tabs.ArticlesSettingsTab;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.myftp.gattserver.grass3.tabs.template.ISettingsTab;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("articlesSettingsTabFactory")
public class ArticlesSettingsTabFactory extends AbstractSettingsTabFactory {

	public ArticlesSettingsTabFactory() {
		super("Články", "articles");
	}

	public boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new ArticlesSettingsTab(request);
	}
}

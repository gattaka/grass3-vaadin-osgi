package org.myftp.gattserver.grass3.articles.tabs.factories;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("articlesSettingsTabFactory")
public class ArticlesSettingsTabFactory extends AbstractSettingsTabFactory {

	public ArticlesSettingsTabFactory() {
		super("Články", "articles", "articlesSettingsTab");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN);
	}
}

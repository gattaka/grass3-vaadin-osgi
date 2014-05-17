package org.myftp.gattserver.grass3.articles.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 9120334766218647141L;

	public ArticlesEditorPageFactory() {
		super("articles-editor", "articlesEditorPage");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null
				&& (getUser().getRoles().contains(Role.ADMIN) || getUser().getRoles().contains(Role.AUTHOR));
	}

}

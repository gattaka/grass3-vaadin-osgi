package cz.gattserver.grass3.articles.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.ui.pages.ArticlesEditorPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends AbstractPageFactory {

	public ArticlesEditorPageFactory() {
		super("articles-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null && (getUser().isAdmin() || getUser().hasRole(Role.AUTHOR));
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new ArticlesEditorPage(request);
	}

}

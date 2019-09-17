package cz.gattserver.grass3.articles.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.ui.pages.ArticlesEditorPage;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends AbstractPageFactory {

	public ArticlesEditorPageFactory() {
		super("articles-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null && (getUser().isAdmin() || getUser().hasRole(CoreRole.AUTHOR));
	}

	@Override
	protected GrassPage createPage() {
		return new ArticlesEditorPage();
	}

}

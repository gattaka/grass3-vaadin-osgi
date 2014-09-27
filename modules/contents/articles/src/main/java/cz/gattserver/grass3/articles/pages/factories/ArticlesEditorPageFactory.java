package cz.gattserver.grass3.articles.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.pages.ArticlesEditorPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 9120334766218647141L;

	public ArticlesEditorPageFactory() {
		super("articles-editor");
	}

	@Override
	protected boolean isAuthorized() {
		return getUser() != null
				&& (getUser().getRoles().contains(Role.ADMIN) || getUser().getRoles().contains(Role.AUTHOR));
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new ArticlesEditorPage(request);
	}

}

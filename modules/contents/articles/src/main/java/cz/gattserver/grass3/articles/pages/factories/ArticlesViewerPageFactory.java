package cz.gattserver.grass3.articles.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.pages.ArticlesViewerPage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("articlesViewerPageFactory")
public class ArticlesViewerPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -2129382943779873599L;

	public ArticlesViewerPageFactory() {
		super("articles");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new ArticlesViewerPage(request);
	}
}

package cz.gattserver.grass3.articles.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.ui.pages.ArticlesViewerPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("articlesViewerPageFactory")
public class ArticlesViewerPageFactory extends AbstractPageFactory {

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

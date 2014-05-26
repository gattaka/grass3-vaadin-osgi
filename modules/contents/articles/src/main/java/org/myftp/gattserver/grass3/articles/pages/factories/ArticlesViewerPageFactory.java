package org.myftp.gattserver.grass3.articles.pages.factories;

import org.myftp.gattserver.grass3.articles.pages.ArticlesViewerPage;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

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
	protected IGrassPage createPage(GrassRequest request) {
		return new ArticlesViewerPage(request);
	}
}

package org.myftp.gattserver.grass3.articles.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("articlesViewerPageFactory")
public class ArticlesViewerPageFactory extends AbstractPageFactory {

	public ArticlesViewerPageFactory() {
		super("articles", "articlesViewerPage");
	}
	
	@Override
	protected boolean isAuthorized() {
		return true;
	}
}

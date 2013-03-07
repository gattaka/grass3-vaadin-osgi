package org.myftp.gattserver.grass3.articles.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("articlesViewerPageFactory")
public class ArticlesViewerPageFactory extends PageFactory {

	public ArticlesViewerPageFactory() {
		super("articles", "articlesViewerPage");
	}

}

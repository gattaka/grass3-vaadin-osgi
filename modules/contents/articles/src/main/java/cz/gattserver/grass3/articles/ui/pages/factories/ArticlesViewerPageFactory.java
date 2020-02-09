package cz.gattserver.grass3.articles.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("articlesViewerPageFactory")
public class ArticlesViewerPageFactory extends AbstractPageFactory {

	public ArticlesViewerPageFactory() {
		super("articles");
	}

}

package org.myftp.gattserver.grass3.articles.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends AbstractPageFactory {

	public ArticlesEditorPageFactory() {
		super("articles-editor", "articlesEditorPage");
	}

}

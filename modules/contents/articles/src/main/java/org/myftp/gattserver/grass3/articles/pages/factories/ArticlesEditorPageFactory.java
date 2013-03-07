package org.myftp.gattserver.grass3.articles.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends PageFactory {

	public ArticlesEditorPageFactory() {
		super("articles-editor", "articlesEditorPage");
	}

}

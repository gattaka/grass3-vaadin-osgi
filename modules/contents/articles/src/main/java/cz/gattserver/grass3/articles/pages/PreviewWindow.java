package cz.gattserver.grass3.articles.pages;

import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.web.common.window.WebWindow;

public class PreviewWindow extends WebWindow {

	private static final long serialVersionUID = 3575905789676981884L;

	public PreviewWindow(ArticleDTO articleDTO) {
		super("Náhled");

		addComponent(new ArticleContentComponent(articleDTO));

		setWidth("700px");
		setHeight("500px");

	}

}

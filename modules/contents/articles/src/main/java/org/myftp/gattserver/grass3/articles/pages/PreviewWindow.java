package org.myftp.gattserver.grass3.articles.pages;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

public class PreviewWindow extends GrassSubWindow {

	private static final long serialVersionUID = 3575905789676981884L;

	public PreviewWindow(ArticleDTO articleDTO) {
		super("Náhled");

		addComponent(new ArticleContentComponent(articleDTO));

		setWidth("700px");
		setHeight("500px");

	}

}

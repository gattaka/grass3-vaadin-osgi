package org.myftp.gattserver.grass3.articles.service.impl;

import org.myftp.gattserver.grass3.articles.pages.factories.ArticlesEditorPageFactory;
import org.myftp.gattserver.grass3.articles.pages.factories.ArticlesViewerPageFactory;
import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.myftp.gattserver.grass3.service.IContentService;
import org.springframework.stereotype.Component;

import com.vaadin.server.ThemeResource;

@Component("articlesContentService")
public class ArticlesContentService implements IContentService {

	public static final String ID = "org.myftp.gattserver.grass3.articles:0.0.1";

	@javax.annotation.Resource(name = "articlesViewerPageFactory")
	private ArticlesViewerPageFactory articlesViewerPageFactory;

	@javax.annotation.Resource(name = "articlesEditorPageFactory")
	private ArticlesEditorPageFactory articlesEditorPageFactory;

	public String getCreateNewContentLabel() {
		return "Vytvořit nový článek";
	}

	public com.vaadin.server.Resource getContentIcon() {
		return (com.vaadin.server.Resource) new ThemeResource(
				"img/tags/document_16.png");
	}

	public String getContentID() {
		return ID;
	}

	public PageFactory getContentEditorPageFactory() {
		return articlesEditorPageFactory;
	}

	public PageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}

}

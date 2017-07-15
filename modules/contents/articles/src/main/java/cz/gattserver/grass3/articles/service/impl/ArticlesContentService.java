package cz.gattserver.grass3.articles.service.impl;

import org.springframework.stereotype.Component;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.service.IContentService;
import cz.gattserver.web.common.ui.ImageIcons;

@Component("articlesContentService")
public class ArticlesContentService implements IContentService {

	public static final String ID = "cz.gattserver.grass3.articles:0.0.1";

	@javax.annotation.Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

	@javax.annotation.Resource(name = "articlesEditorPageFactory")
	private IPageFactory articlesEditorPageFactory;

	public String getCreateNewContentLabel() {
		return "Vytvořit nový článek";
	}

	public com.vaadin.server.Resource getContentIcon() {
		return (com.vaadin.server.Resource) new ThemeResource(ImageIcons.DOCUMENT_16_ICON);
	}

	public String getContentID() {
		return ID;
	}

	public IPageFactory getContentEditorPageFactory() {
		return articlesEditorPageFactory;
	}

	public IPageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}

}

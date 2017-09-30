package cz.gattserver.grass3.articles.service.impl;

import org.springframework.stereotype.Component;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.web.common.ui.ImageIcons;

@Component("articlesContentService")
public class ArticlesContentService implements ContentService {

	public static final String ID = "cz.gattserver.grass3.articles:0.0.1";

	@javax.annotation.Resource(name = "articlesViewerPageFactory")
	private PageFactory articlesViewerPageFactory;

	@javax.annotation.Resource(name = "articlesEditorPageFactory")
	private PageFactory articlesEditorPageFactory;

	public String getCreateNewContentLabel() {
		return "Vytvořit nový článek";
	}

	public ThemeResource getContentIcon() {
		return new ThemeResource(ImageIcons.DOCUMENT_16_ICON);
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

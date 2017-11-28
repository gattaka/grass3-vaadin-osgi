package cz.gattserver.grass3.modules;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.ui.ImageIcon;

@Component("articlesContentModule")
public class ArticlesContentModule implements ContentModule {

	public static final String ID = "cz.gattserver.grass3.articles:0.0.1";

	@Resource(name = "articlesViewerPageFactory")
	private PageFactory articlesViewerPageFactory;

	@Resource(name = "articlesEditorPageFactory")
	private PageFactory articlesEditorPageFactory;

	public String getCreateNewContentLabel() {
		return "Vytvořit nový článek";
	}

	@Override
	public com.vaadin.server.Resource getContentIcon() {
		return ImageIcon.DOCUMENT_16_ICON.createResource();
	}

	@Override
	public String getContentID() {
		return ID;
	}

	@Override
	public PageFactory getContentEditorPageFactory() {
		return articlesEditorPageFactory;
	}

	@Override
	public PageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}

}

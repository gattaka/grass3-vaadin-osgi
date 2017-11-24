package cz.gattserver.grass3.modules;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.modules.ContentModule;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.ui.ImageIcons;

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

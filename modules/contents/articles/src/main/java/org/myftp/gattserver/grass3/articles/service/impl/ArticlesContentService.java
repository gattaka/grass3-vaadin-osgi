package org.myftp.gattserver.grass3.articles.service.impl;

import org.myftp.gattserver.grass3.articles.windows.ArticlesEditorWindow;
import org.myftp.gattserver.grass3.articles.windows.ArticlesViewerWindow;
import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.windows.template.BaseWindow;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class ArticlesContentService implements IContentService {

	public static final String ID = "org.myftp.gattserver.grass3.articles:0.0.1";

	public BaseWindow getContentEditorWindowNewInstance() {
		return new ArticlesEditorWindow();
	}

	public BaseWindow getContentViewerWindowNewInstance() {
		return new ArticlesViewerWindow();
	}

	public Class<? extends BaseWindow> getContentEditorWindowClass() {
		return ArticlesEditorWindow.class;
	}

	public Class<? extends BaseWindow> getContentViewerWindowClass() {
		return ArticlesViewerWindow.class;
	}

	public String getCreateNewContentLabel() {
		return "Vytvořit nový článek";
	}

	public Resource getContentIcon() {
		return new ThemeResource("img/tags/document_16.png");
	}

	public String getContentID() {
		return ID;
	}

}

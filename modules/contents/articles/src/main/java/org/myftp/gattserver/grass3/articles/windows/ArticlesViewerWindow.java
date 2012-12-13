package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.template.ContentViewerWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ArticlesViewerWindow extends ContentViewerWindow {

	private static final long serialVersionUID = 5078280973817331002L;

	private ArticleDTO article;
	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;

	public ArticlesViewerWindow() {
		super(ArticlesViewerWindow.class);
	}

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {

		super.createLeftColumnContent(layout);
		layout.addComponent(new Label("Levé menu vieweru"));

	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		super.createRightColumnContent(layout);
		layout.addComponent(new Label("Pravé menu vieweru"));

	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(relativeUri);
		if (identifier == null)
			showError404();

		article = articleFacade.getArticleById(identifier.getId());

		return super.handleURI(context, relativeUri);
	}

	@Override
	protected ContentNodeDTO getContentNodeDTO() {
		return article.getContentNode();
	}

}

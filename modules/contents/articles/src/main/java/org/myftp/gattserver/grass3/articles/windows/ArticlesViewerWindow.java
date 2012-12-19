package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.template.ContentViewerWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class ArticlesViewerWindow extends ContentViewerWindow {

	private static final long serialVersionUID = 5078280973817331002L;

	private ArticleDTO article;
	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;
	private Label articleContentLabel;

	public ArticlesViewerWindow() {
		super(ArticlesViewerWindow.class);
		setName("articles");
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(relativeUri);
		if (identifier == null)
			showError404();

		article = articleFacade.getArticleById(identifier.getId());
		articleContentLabel.setValue(article.getOutputHTML());

		return super.handleURI(context, relativeUri);
	}

	@Override
	protected ContentNodeDTO getContentNodeDTO() {
		return article.getContentNode();
	}

	@Override
	protected void createContent(VerticalLayout layout) {
		layout.addComponent(articleContentLabel = new Label("",
				Label.CONTENT_XHTML));
	}

	@Override
	protected void updateOperationsList(HorizontalLayout operationsListLayout) {

		// Uložit
		Button saveButton = new Button("Upravit");
		saveButton.setIcon(new ThemeResource("img/tags/pencil_16.png"));
		saveButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				open(new ExternalResource(getWindow(ArticlesEditorWindow.class)
						.getURL()
						+ DefaultContentOperations.EDIT.toString()
						+ "/"
						+ URLIdentifierUtils.createURLIdentifier(article
								.getId(), article.getContentNode().getName())));

			}

		});
		operationsListLayout.addComponent(saveButton);
	}
}

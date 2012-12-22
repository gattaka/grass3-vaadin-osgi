package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.subwindows.WarnSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.windows.CategoryWindow;
import org.myftp.gattserver.grass3.windows.template.ContentViewerWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

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
		if (article == null)
			showError404();
		
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
	protected void updateOperationsList(CssLayout operationsListLayout) {

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

		// Smazat
		Button deleteButton = new Button("Smazat");
		deleteButton.setIcon(new ThemeResource("img/tags/delete_16.png"));
		deleteButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ConfirmSubwindow confirmSubwindow = new ConfirmSubwindow(
						"Opravdu si přejete smazat tento článek ?") {

					private static final long serialVersionUID = -3214040983143363831L;

					@Override
					protected void onConfirm(ClickEvent event) {

						NodeDTO node = nodeFacade.getNodeById(article
								.getContentNode().getParentID());
						final ExternalResource categoryResource = new ExternalResource(
								ArticlesViewerWindow.this.getWindow(
										CategoryWindow.class).getURL()
										+ URLIdentifierUtils
												.createURLIdentifier(
														node.getId(),
														node.getName()));

						// zdařilo se ? Pokud ano, otevři info okno a při
						// potvrzení jdi na kategorii
						if (articleFacade.deleteArticle(article)) {
							InfoSubwindow infoSubwindow = new InfoSubwindow(
									"Smazání článku proběhlo úspěšně.") {

								private static final long serialVersionUID = -6688396549852552674L;

								protected void onProceed(ClickEvent event) {
									ArticlesViewerWindow.this
											.open(categoryResource);
								};
							};
							ArticlesViewerWindow.this.addWindow(infoSubwindow);
						} else {
							// Pokud ne, otevři warn okno a při
							// potvrzení jdi na kategorii
							WarnSubwindow warnSubwindow = new WarnSubwindow(
									"Smazání článku se nezdařilo.") {

								private static final long serialVersionUID = -6688396549852552674L;

								protected void onProceed(ClickEvent event) {
									ArticlesViewerWindow.this
											.open(categoryResource);
								};
							};
							ArticlesViewerWindow.this.addWindow(warnSubwindow);
						}
						
						// zavři původní confirm okno
						getParent().removeWindow(this);

					}
				};
				addWindow(confirmSubwindow);

			}

		});
		operationsListLayout.addComponent(deleteButton);
	}
}

package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;
import java.util.Arrays;

import org.myftp.gattserver.grass3.articles.PluginServiceHolder;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.facades.ContentTagFacade;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.myftp.gattserver.grass3.windows.CategoryWindow;
import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ArticlesEditorWindow extends TwoColumnWindow {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory
			.getLogger(ArticlesEditorWindow.class);

	private NodeDTO category;
	private ArticleDTO article;
	private NodeFacade nodeFacade = NodeFacade.INSTANCE;
	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;
	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;

	private HorizontalLayout toolsLayout = new HorizontalLayout();
	private VerticalLayout editorTextLayout;
	private final TextArea articleTextArea = new TextArea();
	private final TextField articleKeywords = new TextField();
	private final TextField articleNameField = new TextField();

	private boolean editMode = false;

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout toolsPartLayout = new VerticalLayout();
		layout.addComponent(toolsPartLayout);
		toolsPartLayout.addComponent(new Label("<h2>Nástroje</h2>",
				Label.CONTENT_XHTML));
		toolsPartLayout.addComponent(toolsLayout);
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {
		editorTextLayout = layout;
		editorTextLayout.setSpacing(true);
		editorTextLayout.setMargin(true);
	}

	private void updateToolsMenu() {

		toolsLayout.removeAllComponents();

		/**
		 * Projdi zaregistrované pluginy a vytvoř menu nástrojů
		 */
		for (String group : PluginServiceHolder.getInstance()
				.getRegisteredGroups()) {

			VerticalLayout groupLayout = new VerticalLayout();
			if (group != null)
				groupLayout.addComponent(new Label("<h3>" + group + "</h3>",
						Label.CONTENT_XHTML));
			toolsLayout.addComponent(groupLayout);
			HorizontalLayout groupToolsLayout = new HorizontalLayout();
			groupLayout.addComponent(groupToolsLayout);

			final ReferenceHolder<EditorButtonResources> holder = new ReferenceHolder<EditorButtonResources>();
			for (EditorButtonResources resources : PluginServiceHolder
					.getInstance().getGroupTags(group)) {

				holder.setValue(resources);

				Button button = new Button(resources.getDescription());
				button.setIcon(resources.getImage());
				button.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = 607422393151282918L;

					public void buttonClick(ClickEvent event) {
						executeJavaScript("insert('"
								+ holder.getValue().getPrefix() + "','"
								+ holder.getValue().getSuffix() + "');");
					}
				});
				groupToolsLayout.addComponent(button);
			}
		}

	}

	private void updateEditorTextPart() {

		editorTextLayout.removeAllComponents();

		VerticalLayout articleNameLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleNameLayout);
		articleNameLayout.addComponent(new Label("<h2>Název článku</h2>",
				Label.CONTENT_XHTML));
		articleNameLayout.addComponent(articleNameField);
		articleNameField.setWidth("100%");

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleKeywordsLayout);
		articleKeywordsLayout.addComponent(new Label("<h2>Klíčová slova</h2>",
				Label.CONTENT_XHTML));
		articleKeywordsLayout.addComponent(articleKeywords);
		articleKeywords.setWidth("100%");

		VerticalLayout articleContentLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleContentLayout);
		articleContentLayout.addComponent(new Label("<h2>Obsah článku</h2>",
				Label.CONTENT_XHTML));
		articleContentLayout.addComponent(articleTextArea);
		articleTextArea.setSizeFull();

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);
		editorTextLayout.addComponent(buttonLayout);

		// Náhled
		Button previewButton = new Button("Náhled");
		previewButton.setIcon(new ThemeResource("img/tags/document_16.png"));
		previewButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ArticleDTO articleDTO = articleFacade.processPreview(String
						.valueOf(articleTextArea.getValue()));

				PreviewWindow previewWindow = new PreviewWindow(articleDTO);
				addWindow(previewWindow);
			}

		});
		buttonLayout.addComponent(previewButton);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon(new ThemeResource("img/tags/save_16.png"));
		saveButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				boolean success;
				Long id = null;
				if (editMode) {
					success = articleFacade.modifyArticle(
							String.valueOf(articleNameField.getValue()),
							String.valueOf(articleTextArea.getValue()),
							String.valueOf(articleKeywords.getValue()), article);
				} else {
					id = articleFacade.saveArticle(
							String.valueOf(articleNameField.getValue()),
							String.valueOf(articleTextArea.getValue()),
							String.valueOf(articleKeywords.getValue()),
							category, getApplication().getUser());
					success = id != null;

					// odteď budeme editovat
					editMode = true;
					article = articleFacade.getArticleById(id);
				}

				if (success) {
					showInfo(editMode ? "Úprava článku proběhla úspěšně"
							: "Uložení článku proběhlo úspěšně");
				} else {
					showWarning(editMode ? "Úprava článku se nezdařila"
							: "Uložení článku se nezdařilo");
				}

			}

		});
		buttonLayout.addComponent(saveButton);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon(new ThemeResource("img/tags/save_16.png"));
		saveAndCloseButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				boolean success;
				Long id = null;
				if (editMode) {
					success = articleFacade.modifyArticle(
							String.valueOf(articleNameField.getValue()),
							String.valueOf(articleTextArea.getValue()),
							String.valueOf(articleKeywords.getValue()), article);
				} else {
					id = articleFacade.saveArticle(
							String.valueOf(articleNameField.getValue()),
							String.valueOf(articleTextArea.getValue()),
							String.valueOf(articleKeywords.getValue()),
							category, getApplication().getUser());
					success = id != null;
				}

				final Long articleId = editMode ? article.getId() : id;
				if (success) {
					InfoSubwindow infoSubwindow = new InfoSubwindow(
							editMode ? "Úprava článku proběhla úspěšně"
									: "Uložení článku proběhlo úspěšně") {

						private static final long serialVersionUID = -4517297931117830104L;

						protected void onProceed(ClickEvent event) {
							ArticlesEditorWindow.this
									.open(new ExternalResource(
											ArticlesEditorWindow.this
													.getWindow(
															ArticlesViewerWindow.class)
													.getURL()
													+ URLIdentifierUtils
															.createURLIdentifier(
																	articleId,
																	String.valueOf(articleNameField
																			.getValue()))));
						};
					};
					addWindow(infoSubwindow);
				} else {
					showWarning(editMode ? "Úprava článku se nezdařila"
							: "Uložení článku se nezdařilo");
				}

			}

		});
		buttonLayout.addComponent(saveAndCloseButton);

		// Zrušit
		Button cancelButton = new Button("Zrušit");
		cancelButton.setIcon(new ThemeResource("img/tags/delete_16.png"));
		cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ConfirmSubwindow confirmSubwindow = new ConfirmSubwindow(
						"Opravdu si přejete zavřít editor článku ? Veškeré neuložené změny budou ztraceny.") {

					private static final long serialVersionUID = -3214040983143363831L;

					@Override
					protected void onConfirm(ClickEvent event) {
						ArticlesEditorWindow.this.open(new ExternalResource(
								ArticlesEditorWindow.this.getWindow(
										CategoryWindow.class).getURL()
										+ URLIdentifierUtils
												.createURLIdentifier(
														category.getId(),
														category.getName())));
					}
				};
				addWindow(confirmSubwindow);

			}

		});
		buttonLayout.addComponent(cancelButton);
	}

	@Override
	protected void onShow() {

		updateToolsMenu();
		updateEditorTextPart();

		// editor.js
		StringBuilder loadScript = new StringBuilder();
		loadScript
				.append("var head= document.getElementsByTagName('head')[0];")
				.append("var script= document.createElement('script');")
				.append("script.type= 'text/javascript';")
				.append("script.src= '/VAADIN/themes/grass/articles/js/editor.js';")
				.append("head.appendChild(script);");
		executeJavaScript(loadScript.toString());

		articleTextArea.setValue("");

		super.onShow();
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		String[] parts = relativeUri.split("/");
		if (parts.length < 2) {
			logger.debug("Chybí operace nebo identifikátor cíle");
			showError404();
		}

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(parts[1]);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '"
					+ parts[1] + "'");
			showError404();
		}

		// operace ?
		if (parts[0].equals(DefaultContentOperations.NEW.toString())) {
			editMode = false;
			category = nodeFacade.getNodeById(identifier.getId());
			articleNameField.setValue("");
			articleKeywords.setValue("");
		} else if (parts[0].equals(DefaultContentOperations.EDIT.toString())) {
			editMode = true;
			article = articleFacade.getArticleById(identifier.getId());
			articleNameField.setValue(article.getContentNode().getName());
			Object[] tagsArray = article.getContentNode().getContentTags()
					.toArray();
			articleKeywords.setValue(contentTagFacade.serializeTags(Arrays
					.copyOf(tagsArray, tagsArray.length, String[].class)));
			articleTextArea.setValue(article.getText());
		} else {
			logger.debug("Neznámá operace: '" + parts[0] + "'");
			showError404();
		}

		return super.handleURI(context, relativeUri);
	}
}

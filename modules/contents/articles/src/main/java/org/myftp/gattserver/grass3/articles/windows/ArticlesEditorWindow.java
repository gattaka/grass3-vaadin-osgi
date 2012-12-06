package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;

import org.myftp.gattserver.grass3.articles.PluginServiceHolder;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.util.CategoryUtils;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.myftp.gattserver.grass3.windows.CategoryWindow;
import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;

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

	private NodeDTO category;
	private NodeFacade nodeFacade = NodeFacade.INSTANCE;
	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;

	private HorizontalLayout toolsLayout = new HorizontalLayout();
	private VerticalLayout editorTextLayout;
	private final TextArea articleTextArea = new TextArea();
	private final TextField articleKeywords = new TextField();
	private final TextField articleNameField = new TextField();

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

				if (articleFacade.saveArticle(
						String.valueOf(articleNameField.getValue()),
						String.valueOf(articleTextArea.getValue()),
						String.valueOf(articleKeywords.getValue()), category,
						getApplication().getUser())) {
					showInfo("Uložení článku se zdařilo");
				} else {
					showWarning("Uložení článku se nezdařilo");
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

				if (articleFacade.saveArticle(
						String.valueOf(articleNameField.getValue()),
						String.valueOf(articleTextArea.getValue()),
						String.valueOf(articleKeywords.getValue()), category,
						getApplication().getUser())) {
					InfoSubwindow infoSubwindow = new InfoSubwindow(
							"Uložení článku proběhlo úspěšně") {

						private static final long serialVersionUID = -4517297931117830104L;

						protected void onProceed(ClickEvent event) {
							ArticlesEditorWindow.this
									.open(new ExternalResource(
											ArticlesEditorWindow.this
													.getWindow(
															CategoryWindow.class)
													.getURL()
													+ CategoryUtils
															.createURLIdentifier(category)));
						};
					};
					addWindow(infoSubwindow);
				} else {
					showWarning("Uložení článku se nezdařilo");
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
										+ CategoryUtils
												.createURLIdentifier(category)));
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

		category = CategoryUtils.parseURLIdentifier(relativeUri);
		if (category == null)
			showError404();

		category = nodeFacade.getNodeById(category.getId());

		return super.handleURI(context, relativeUri);
	}

}

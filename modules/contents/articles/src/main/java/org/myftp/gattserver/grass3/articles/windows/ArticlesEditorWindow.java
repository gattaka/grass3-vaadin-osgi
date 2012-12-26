package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.myftp.gattserver.grass3.articles.PluginServiceHolder;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.facades.ContentTagFacade;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ArticlesEditorWindow extends TwoColumnWindow {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory
			.getLogger(ArticlesEditorWindow.class);

	private NodeDTO category;
	private ArticleDTO article;
	private NodeFacade nodeFacade = NodeFacade.INSTANCE;
	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;
	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;

	private VerticalLayout toolsLayout = new VerticalLayout();
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
		toolsLayout.setWidth("100%");
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {
		editorTextLayout = layout;
		editorTextLayout.setSpacing(true);
		editorTextLayout.setMargin(true);
	}

	private void updateToolsMenu() {

		toolsLayout.removeAllComponents();

		List<String> groups = new ArrayList<String>(PluginServiceHolder
				.getInstance().getRegisteredGroups());
		Collections.sort(groups, new Comparator<String>() {

			// odolné vůči null
			public int compare(String o1, String o2) {
				if (o1 == null) {
					if (o2 == null)
						return 0; // stejné
					else
						return "".compareTo(o2); // první ber jako prázdný
				} else {
					if (o2 == null)
						return o1.compareTo(""); // druhý ber jako prázdný
					else
						return o1.compareTo(o2); // ani jeden není null
				}
			}

		});

		/**
		 * Projdi zaregistrované pluginy a vytvoř menu nástrojů
		 */
		for (String group : groups) {

			VerticalLayout groupLayout = new VerticalLayout();
			groupLayout.setWidth("100%");
			if (group != null)
				groupLayout.addComponent(new Label("<h3>" + group + "</h3>",
						Label.CONTENT_XHTML));
			toolsLayout.addComponent(groupLayout);

			CssLayout groupToolsLayout = new CssLayout();
			groupToolsLayout.addStyleName("tools_css_menu");
			groupToolsLayout.setWidth("100%");
			groupLayout.addComponent(groupToolsLayout);

			List<EditorButtonResources> resourcesBundles = new ArrayList<EditorButtonResources>(
					PluginServiceHolder.getInstance().getGroupTags(group));
			Collections.sort(resourcesBundles);

			final ReferenceHolder<EditorButtonResources> holder = new ReferenceHolder<EditorButtonResources>();
			for (EditorButtonResources resourceBundle : resourcesBundles) {

				holder.setValue(resourceBundle);

				Button button = new Button(resourceBundle.getDescription());
				button.setIcon(resourceBundle.getImage());
				button.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = 607422393151282918L;

					// potřeba, jinak se bude linkovat reference na poslední
					// holder z vnější instance ;)
					String prefix = holder.getValue().getPrefix();
					String suffix = holder.getValue().getSuffix();

					public void buttonClick(ClickEvent event) {
						executeJavaScript("insert('" + prefix + "','" + suffix
								+ "');");
					}
				});
				groupToolsLayout.addComponent(button);
			}
		}

	}

	private void updateContentNamePart() {

		VerticalLayout articleNameLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleNameLayout);
		articleNameLayout.addComponent(new Label("<h2>Název článku</h2>",
				Label.CONTENT_XHTML));
		articleNameLayout.addComponent(articleNameField);
		articleNameField.setWidth("100%");

	}

	private void updateContentKeywordsPart() {

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleKeywordsLayout);

		// label
		articleKeywordsLayout.addComponent(new Label("<h2>Klíčová slova</h2>",
				Label.CONTENT_XHTML));

		// menu tagů + textfield tagů
		HorizontalLayout keywordsMenuAndTextLayout = new HorizontalLayout();
		keywordsMenuAndTextLayout.setWidth("100%");
		keywordsMenuAndTextLayout.setSpacing(true);
		articleKeywordsLayout.addComponent(keywordsMenuAndTextLayout);

		// menu
		keywordsMenuAndTextLayout.addComponent(new Button("Vybrat",
				new Button.ClickListener() {

					private static final long serialVersionUID = -3160636656140236427L;

					public void buttonClick(ClickEvent event) {

						final Window keywordsMenuSubwindow = new GrassSubWindow(
								"Vybrat klíčová slova");
						VerticalLayout subWindowLayout = (VerticalLayout) keywordsMenuSubwindow
								.getContent();
						subWindowLayout.setSpacing(true);
						subWindowLayout.setMargin(true);

						final ListSelect list = new ListSelect();
						list.setWidth("100%");
						list.setRows(10);
						list.setNullSelectionAllowed(true);
						list.setMultiSelect(true);
						list.setImmediate(true);

						for (ContentTagDTO contentTag : contentTagFacade
								.getAllContentTags()) {
							list.addItem(contentTag);
							list.setItemCaption(contentTag,
									contentTag.getName());
						}

						keywordsMenuSubwindow.addComponent(list);
						keywordsMenuSubwindow.addComponent(new Button("Přidat",
								new Button.ClickListener() {

									private static final long serialVersionUID = -4544649471207273304L;

									public void buttonClick(ClickEvent event) {

										@SuppressWarnings("unchecked")
										Collection<ContentTagDTO> values = (Collection<ContentTagDTO>) list
												.getValue();
										StringBuffer stringBuffer = new StringBuffer();
										String oldValue = (String) articleKeywords
												.getValue();
										stringBuffer.append(oldValue);
										for (ContentTagDTO tagDTO : values) {
											if (stringBuffer.length() != 0)
												stringBuffer.append(", ");
											stringBuffer.append(tagDTO
													.getName());
										}
										articleKeywords.setValue(stringBuffer
												.toString());

										removeWindow(keywordsMenuSubwindow);

									}
								}));

						addWindow(keywordsMenuSubwindow);
					}
				}));

		// textfield
		keywordsMenuAndTextLayout.addComponent(articleKeywords);
		keywordsMenuAndTextLayout.setExpandRatio(articleKeywords, 1);
		articleKeywords.setWidth("100%");

	}

	private void updateContentTextPart() {

		VerticalLayout articleContentLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleContentLayout);
		articleContentLayout.addComponent(new Label("<h2>Obsah článku</h2>",
				Label.CONTENT_XHTML));
		articleContentLayout.addComponent(articleTextArea);
		articleTextArea.setSizeFull();
		articleTextArea.setRows(30);

	}

	private void updateEditorTextPart() {

		editorTextLayout.removeAllComponents();

		// název článku
		updateContentNamePart();

		// klíčová slova
		updateContentKeywordsPart();

		// text
		updateContentTextPart();

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

				// pokud se bude měnit
				boolean oldMode = editMode;
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
					showInfo(oldMode ? "Úprava článku proběhla úspěšně"
							: "Uložení článku proběhlo úspěšně");
				} else {
					showWarning(oldMode ? "Úprava článku se nezdařila"
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
							returnToArticle(articleId);
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
						// ruším úpravu existujícího článku (vracím se na
						// článek), nebo nového (vracím se do kategorie) ?
						if (editMode) {
							returnToArticle();
						} else {
							returnToCategory();
						}
					}
				};
				addWindow(confirmSubwindow);

			}

		});
		buttonLayout.addComponent(cancelButton);
	}

	/**
	 * Zavolá vrácení se na článek dle daného id (nový článek, upravovaný
	 * článek...)
	 */
	private void returnToArticle(Long articleId) {
		ArticlesEditorWindow.this.open(new ExternalResource(
				ArticlesEditorWindow.this.getWindow(ArticlesViewerWindow.class)
						.getURL()
						+ URLIdentifierUtils.createURLIdentifier(articleId,
								article.getContentNode().getName())));
	}

	/**
	 * Zavolá vrácení se na upravovaný článek
	 */
	private void returnToArticle() {
		returnToArticle(article.getId());
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToCategory() {
		ArticlesEditorWindow.this.open(new ExternalResource(
				ArticlesEditorWindow.this.getWindow(CategoryWindow.class)
						.getURL()
						+ URLIdentifierUtils.createURLIdentifier(
								category.getId(), category.getName())));
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
			articleTextArea.setValue("");
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

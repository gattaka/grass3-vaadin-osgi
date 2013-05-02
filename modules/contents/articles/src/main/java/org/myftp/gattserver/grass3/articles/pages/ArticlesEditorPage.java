package org.myftp.gattserver.grass3.articles.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.IPluginServiceHolder;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.facade.IArticleFacade;
import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.js.GrassJSBootstrapComponent;
import org.myftp.gattserver.grass3.js.JQueryBootstrapComponent;
import org.myftp.gattserver.grass3.js.JQueryUIBootstrapComponent;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.JQueryAccordion;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@org.springframework.stereotype.Component("articlesEditorPage")
@Scope("prototype")
public class ArticlesEditorPage extends TwoColumnPage {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory
			.getLogger(ArticlesEditorPage.class);

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "articleFacade")
	private IArticleFacade articleFacade;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "pluginServiceHolder")
	private IPluginServiceHolder pluginServiceHolder;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

	private NodeDTO category;
	private ArticleDTO article;

	private final TextArea articleTextArea = new TextArea();
	private final TextField articleKeywords = new TextField();
	private final TextField articleNameField = new TextField();
	private final CheckBox publicatedCheckBox = new CheckBox();

	private boolean editMode = false;

	public ArticlesEditorPage(GrassRequest request) {
		super(request);
		JavaScript
				.eval("window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	@Override
	protected void submitInitJS(Set<String> initJS) {
		super.submitInitJS(initJS);

		// jQueryUI JS
		initJS.add(getRequest().getContextRoot()
				+ "/VAADIN/themes/grass/js/jquery-ui.js");

		// jQueryUI CSS
		StringBuilder loadStylesheet = new StringBuilder();
		loadStylesheet
				.append("var head= document.getElementsByTagName('head')[0];")
				.append("var link= document.createElement('link');")
				.append("link.type= 'text/css';")
				.append("link.rel= 'stylesheet';").append("link.href= '")
				.append(getRequest().getContextRoot())
				.append("/VAADIN/themes/grass/js/humanity/jquery-ui.css';")
				.append("head.appendChild(link);");
		JavaScript.getCurrent().execute(loadStylesheet.toString());

	}

	@Override
	protected void init() {

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getCurrentPathToken();
		String identifierToken = analyzer.getCurrentPathToken(1);
		if (operationToken == null || identifierToken == null) {
			logger.debug("Chybí operace nebo identifikátor cíle");
			showError404();
		}

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '"
					+ identifierToken + "'");
			showError404();
		}

		// operace ?
		if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
			editMode = false;
			category = nodeFacade.getNodeByIdForOverview(identifier.getId());
			articleNameField.setValue("");
			articleKeywords.setValue("");
			articleTextArea.setValue("");
			publicatedCheckBox.setValue(true);
		} else if (operationToken.equals(DefaultContentOperations.EDIT
				.toString())) {
			editMode = true;
			article = articleFacade.getArticleForDetail(identifier.getId());
			articleNameField.setValue(article.getContentNode().getName());
			articleKeywords.setValue(contentTagFacade.serializeTags(article
					.getContentNode().getContentTags()));
			articleTextArea.setValue(article.getText());
			publicatedCheckBox
					.setValue(article.getContentNode().isPublicated());
		} else {
			logger.debug("Neznámá operace: '" + operationToken + "'");
			showError404();
		}

		if ((article == null || article.getContentNode().getAuthor()
				.equals(getGrassUI().getUser()))
				|| getGrassUI().getUser().getRoles().contains(Role.ADMIN)) {
			super.init();
		} else {
			// nemá oprávnění upravovat tento článek
			showError403();
			return;
		}

	}

	@Override
	protected Component createLeftColumnContent() {

		// VerticalLayout layout = new VerticalLayout();
		//
		// layout.setSpacing(true);
		// layout.setMargin(true);
		//
		// VerticalLayout toolsPartLayout = new VerticalLayout();
		// layout.addComponent(toolsPartLayout);
		// toolsPartLayout.addComponent(new Label("<h2>Nástroje</h2>",
		// ContentMode.HTML));
		//
		// VerticalLayout toolsLayout = new VerticalLayout();
		// toolsPartLayout.addComponent(toolsLayout);
		// toolsLayout.setWidth("100%");

		List<String> groups = new ArrayList<String>(
				pluginServiceHolder.getRegisteredGroups());
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
		JQueryAccordion accordion = null;
		try {
			accordion = new JQueryAccordion(groups);
		} catch (IOException e) {
			// nemělo by se stát
			e.printStackTrace();
		}
		for (String group : groups) {

			// VerticalLayout groupLayout = new VerticalLayout();
			// groupLayout.setWidth("100%");
			// if (group != null)
			// groupLayout.addComponent(new Label("<h3>" + group + "</h3>",
			// ContentMode.HTML));
			// toolsLayout.addComponent(groupLayout);
			// toolsLayout.addComponent(accordion);

			CssLayout groupToolsLayout = new CssLayout();
			groupToolsLayout.addStyleName("tools_css_menu");
			groupToolsLayout.setWidth("100%");
			// groupLayout.addComponent(groupToolsLayout);
			accordion.setNextElement(groupToolsLayout);

			List<EditorButtonResources> resourcesBundles = new ArrayList<EditorButtonResources>(
					pluginServiceHolder.getGroupTags(group));
			Collections.sort(resourcesBundles);

			final ReferenceHolder<EditorButtonResources> holder = new ReferenceHolder<EditorButtonResources>();
			for (EditorButtonResources resourceBundle : resourcesBundles) {

				holder.setValue(resourceBundle);

				Button button = new Button(resourceBundle.getDescription());
				button.setIcon((com.vaadin.server.Resource) resourceBundle
						.getImage());
				button.addClickListener(new Button.ClickListener() {

					private static final long serialVersionUID = 607422393151282918L;

					// potřeba, jinak se bude linkovat reference na poslední
					// holder z vnější instance ;)
					String prefix = holder.getValue().getPrefix();
					String suffix = holder.getValue().getSuffix();

					public void buttonClick(ClickEvent event) {
						JavaScript.getCurrent().execute(
								"insert('" + prefix + "','" + suffix + "');");
					}
				});
				groupToolsLayout.addComponent(button);
			}
		}

		// jQueryUI Accordion render start
		JavaScript
				.eval("$( \"#accordion\" ).accordion({ event: \"click\", heightStyle: \"content\" });");
		JavaScript
				.eval("$(\".ui-accordion-content\").css(\"padding\",\"1em 1em\");");

		return accordion;
	}

	@Override
	protected Component createRightColumnContent() {

		VerticalLayout editorTextLayout = new VerticalLayout();
		editorTextLayout.setSpacing(true);
		editorTextLayout.setMargin(true);

		// editor.js
		StringBuilder loadScript = new StringBuilder();
		loadScript
				.append("var head= document.getElementsByTagName('head')[0];")
				.append("var script= document.createElement('script');")
				.append("script.type= 'text/javascript';")
				.append("script.src= '").append(getRequest().getContextRoot())
				.append("/VAADIN/themes/grass/articles/js/editor.js';")
				.append("head.appendChild(script);");
		JavaScript.getCurrent().execute(loadScript.toString());

		VerticalLayout articleNameLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleNameLayout);
		articleNameLayout.addComponent(new Label("<h2>Název článku</h2>",
				ContentMode.HTML));
		articleNameLayout.addComponent(articleNameField);
		articleNameField.setWidth("100%");

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleKeywordsLayout);

		// label
		articleKeywordsLayout.addComponent(new Label("<h2>Klíčová slova</h2>",
				ContentMode.HTML));

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

						List<ContentTagDTO> contentTags = contentTagFacade
								.getContentTagsForOverview();
						Collections.sort(contentTags,
								new Comparator<ContentTagDTO>() {

									public int compare(ContentTagDTO o1,
											ContentTagDTO o2) {
										return o1.getName().compareTo(
												o2.getName());
									}
								});

						for (ContentTagDTO contentTag : contentTags) {
							list.addItem(contentTag);
							list.setItemCaption(contentTag,
									contentTag.getName());
						}

						VerticalLayout subwindowLayout = (VerticalLayout) keywordsMenuSubwindow
								.getContent();

						subwindowLayout.addComponent(list);
						subwindowLayout.addComponent(new Button("Přidat",
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

										getUI().removeWindow(
												keywordsMenuSubwindow);

									}
								}));

						getUI().addWindow(keywordsMenuSubwindow);
					}
				}));

		// textfield
		keywordsMenuAndTextLayout.addComponent(articleKeywords);
		keywordsMenuAndTextLayout.setExpandRatio(articleKeywords, 1);
		articleKeywords.setWidth("100%");

		VerticalLayout articleContentLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleContentLayout);
		articleContentLayout.addComponent(new Label("<h2>Obsah článku</h2>",
				ContentMode.HTML));
		articleContentLayout.addComponent(articleTextArea);
		articleTextArea.setSizeFull();
		articleTextArea.setRows(30);

		VerticalLayout articleOptionsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleOptionsLayout);
		articleOptionsLayout.addComponent(new Label(
				"<h2>Nastavení článku</h2>", ContentMode.HTML));

		publicatedCheckBox.setCaption("Publikovat článek");
		publicatedCheckBox
				.setDescription("Je-li prázdné, uvidí článek pouze jeho autor");
		publicatedCheckBox.setImmediate(true);
		articleOptionsLayout.addComponent(publicatedCheckBox);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(new MarginInfo(true, false, false, false));
		editorTextLayout.addComponent(buttonLayout);

		// Náhled
		Button previewButton = new Button("Náhled");
		previewButton.setIcon((com.vaadin.server.Resource) new ThemeResource(
				"img/tags/document_16.png"));
		previewButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ArticleDTO articleDTO = articleFacade.processPreview(
						String.valueOf(articleTextArea.getValue()),
						getRequest().getContextRoot());

				PreviewWindow previewWindow = new PreviewWindow(articleDTO);
				getUI().addWindow(previewWindow);
			}

		});
		buttonLayout.addComponent(previewButton);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon((com.vaadin.server.Resource) new ThemeResource(
				"img/tags/save_16.png"));
		saveButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				if (isFormValid() == false)
					return;

				// pokud se bude měnit
				boolean oldMode = editMode;

				if (saveOrUpdateArticle()) {
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
		saveAndCloseButton
				.setIcon((com.vaadin.server.Resource) new ThemeResource(
						"img/tags/save_16.png"));
		saveAndCloseButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				if (isFormValid() == false)
					return;

				if (saveOrUpdateArticle()) {
					InfoSubwindow infoSubwindow = new InfoSubwindow(
							editMode ? "Úprava článku proběhla úspěšně"
									: "Uložení článku proběhlo úspěšně") {

						private static final long serialVersionUID = -4517297931117830104L;

						protected void onProceed(ClickEvent event) {
							returnToArticle();
						};
					};
					getUI().addWindow(infoSubwindow);
				} else {
					showWarning(editMode ? "Úprava článku se nezdařila"
							: "Uložení článku se nezdařilo");
				}

			}

		});
		buttonLayout.addComponent(saveAndCloseButton);

		// Zrušit
		Button cancelButton = new Button("Zrušit");
		cancelButton.setIcon((com.vaadin.server.Resource) new ThemeResource(
				"img/tags/delete_16.png"));
		cancelButton.addClickListener(new Button.ClickListener() {

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
				getUI().addWindow(confirmSubwindow);

			}

		});
		buttonLayout.addComponent(cancelButton);

		return editorTextLayout;
	}

	private boolean isFormValid() {

		String name = articleNameField.getValue();

		if (name == null || name.isEmpty()) {
			showWarning("Název článku nemůže být prázdný");
			return false;
		}

		return true;
	}

	private boolean saveOrUpdateArticle() {

		if (editMode) {
			return articleFacade.modifyArticle(String.valueOf(articleNameField
					.getValue()), String.valueOf(articleTextArea.getValue()),
					String.valueOf(articleKeywords.getValue()),
					publicatedCheckBox.getValue(), article, getRequest()
							.getContextRoot());
		} else {
			Long id = articleFacade.saveArticle(String.valueOf(articleNameField
					.getValue()), String.valueOf(articleTextArea.getValue()),
					String.valueOf(articleKeywords.getValue()),
					publicatedCheckBox.getValue(), category, getGrassUI()
							.getUser(), getRequest().getContextRoot());

			if (id == null)
				return false;

			// odteď budeme editovat
			editMode = true;
			article = articleFacade.getArticleForDetail(id);
			return true;
		}
	}

	/**
	 * Zavolá vrácení se na článek
	 */
	private void returnToArticle() {
		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(articlesViewerPageFactory,
				URLIdentifierUtils.createURLIdentifier(article.getId(), article
						.getContentNode().getName())));
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToCategory() {
		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(categoryPageFactory,
				URLIdentifierUtils.createURLIdentifier(category.getId(),
						category.getName())));
	}

}

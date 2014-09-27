package cz.gattserver.grass3.articles.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.tokenfield.TokenField;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.IPluginServiceHolder;
import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.facade.IArticleFacade;
import cz.gattserver.grass3.articles.parser.PartsFinder;
import cz.gattserver.grass3.facades.IContentTagFacade;
import cz.gattserver.grass3.facades.INodeFacade;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pages.template.JScriptItem;
import cz.gattserver.grass3.pages.template.TwoColumnPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.subwindows.ConfirmWindow;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.grass3.ui.util.JQueryAccordion;
import cz.gattserver.grass3.util.URLIdentifierUtils;
import cz.gattserver.grass3.util.URLPathAnalyzer;

public class ArticlesEditorPage extends TwoColumnPage {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);

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

	private TextArea articleTextArea;
	private TokenField articleKeywords;
	private TextField articleNameField;
	private CheckBox publicatedCheckBox;

	private boolean editMode;
	private PartsFinder.Result parts;

	public ArticlesEditorPage(GrassRequest request) {
		super(request);
		JavaScript
				.eval("window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	@Override
	protected void init() {

		articleTextArea = new TextArea();
		articleKeywords = new TokenField();
		articleNameField = new TextField();
		publicatedCheckBox = new CheckBox();

		editMode = false;
		parts = null;

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getCurrentPathToken();
		String identifierToken = analyzer.getCurrentPathToken(1);
		String partNumberToken = analyzer.getCurrentPathToken(2);
		if (operationToken == null || identifierToken == null) {
			logger.debug("Chybí operace nebo identifikátor cíle");
			showError404();
		}

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '" + identifierToken + "'");
			showError404();
		}

		// operace ?
		if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
			editMode = false;
			category = nodeFacade.getNodeByIdForOverview(identifier.getId());
			articleNameField.setValue("");
			articleTextArea.setValue("");
			publicatedCheckBox.setValue(true);

		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {

			editMode = true;
			article = articleFacade.getArticleForDetail(identifier.getId());
			articleNameField.setValue(article.getContentNode().getName());

			for (ContentTagDTO tagDTO : article.getContentNode().getContentTags()) {
				articleKeywords.addToken(tagDTO.getName());
			}

			publicatedCheckBox.setValue(article.getContentNode().isPublicated());

			int partNumber;
			if (partNumberToken != null && (partNumber = Integer.valueOf(partNumberToken)) >= 0) {

				try {
					parts = PartsFinder.findParts(new ByteArrayInputStream(article.getText().getBytes("UTF-8")),
							partNumber);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					showError500();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					showError500();
					return;
				}
				articleTextArea.setValue(parts.getTargetPart());
			} else {
				articleTextArea.setValue(article.getText());
			}
		} else {
			logger.debug("Neznámá operace: '" + operationToken + "'");
			showError404();
			return;
		}

		if ((article == null || article.getContentNode().getAuthor().equals(getGrassUI().getUser()))
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

		List<String> groups = new ArrayList<String>(pluginServiceHolder.getRegisteredGroups());
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

			CssLayout groupToolsLayout = new CssLayout();
			groupToolsLayout.addStyleName("tools_css_menu");
			groupToolsLayout.setWidth("100%");
			accordion.setNextElement(groupToolsLayout);

			List<EditorButtonResources> resourcesBundles = new ArrayList<EditorButtonResources>(
					pluginServiceHolder.getGroupTags(group));
			Collections.sort(resourcesBundles);

			for (EditorButtonResources resourceBundle : resourcesBundles) {

				String prefix = resourceBundle.getPrefix();
				String suffix = resourceBundle.getSuffix();

				StringBuilder builder = new StringBuilder();

				builder.append("<div onClick=\"insert('" + prefix + "','" + suffix
						+ "');\" tabindex=\"0\" role=\"button\" class=\"v-button v-widget\">");
				builder.append("<span class=\"v-button-wrap\">");
				if (resourceBundle.getImage() != null) {
					builder.append("<img src=\"" + getRequest().getContextRoot() + "/VAADIN/themes/grass/"
							+ resourceBundle.getImage().toString() + "\"/> ");
				}
				builder.append("<span class=\"v-button-caption\" style=\"vertical-align: super;\">"
						+ resourceBundle.getDescription() + "</span>");
				builder.append("</span>");
				builder.append("</div>");
				Label btnLabel = new Label(builder.toString(), ContentMode.HTML);
				btnLabel.setWidth(null);
				groupToolsLayout.addComponent(btnLabel);

			}
		}

		// jQueryUI CSS
		loadCSS(getRequest().getContextRoot() + "/VAADIN/themes/grass/js/humanity/jquery-ui.css");

		// jQueryUI JS + jQueryUI Accordion render start
		loadJS(new JScriptItem[] { new JScriptItem("js/jquery-ui.js"),
				new JScriptItem("$( \"#accordion\" ).accordion({ event: \"click\", heightStyle: \"content\" })", true),
				new JScriptItem("$(\".ui-accordion-content\").css(\"padding\",\"1em 1em\")", true) });

		return accordion;
	}

	@Override
	protected Component createRightColumnContent() {

		VerticalLayout editorTextLayout = new VerticalLayout();
		editorTextLayout.setSpacing(true);
		editorTextLayout.setMargin(true);

		// editor.js
		loadJS(new JScriptItem("articles/js/editor.js"));

		VerticalLayout articleNameLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleNameLayout);
		articleNameLayout.addComponent(new Label("<h2>Název článku</h2>", ContentMode.HTML));
		articleNameLayout.addComponent(articleNameField);
		articleNameField.setWidth("100%");

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleKeywordsLayout);

		// label
		articleKeywordsLayout.addComponent(new Label("<h2>Klíčová slova</h2>", ContentMode.HTML));

		// menu tagů + textfield tagů
		// http://marc.virtuallypreinstalled.com/TokenField/
		HorizontalLayout keywordsMenuAndTextLayout = new HorizontalLayout();
		keywordsMenuAndTextLayout.setWidth("100%");
		keywordsMenuAndTextLayout.setSpacing(true);
		articleKeywordsLayout.addComponent(keywordsMenuAndTextLayout);

		keywordsMenuAndTextLayout.addComponent(articleKeywords);

		List<ContentTagDTO> contentTags = contentTagFacade.getContentTagsForOverview();
		BeanContainer<String, ContentTagDTO> tokens = new BeanContainer<String, ContentTagDTO>(ContentTagDTO.class);
		tokens.setBeanIdProperty("name");
		tokens.addAll(contentTags);

		articleKeywords.setStyleName(TokenField.STYLE_TOKENFIELD);
		articleKeywords.setContainerDataSource(tokens);
		articleKeywords.setFilteringMode(FilteringMode.CONTAINS); // suggest
		articleKeywords.setTokenCaptionPropertyId("name");
		articleKeywords.setInputPrompt("klíčové slovo");
		articleKeywords.setRememberNewTokens(false);
		articleKeywords.isEnabled();

		VerticalLayout articleContentLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleContentLayout);
		articleContentLayout.addComponent(new Label("<h2>Obsah článku</h2>", ContentMode.HTML));
		articleContentLayout.addComponent(articleTextArea);
		articleTextArea.setSizeFull();
		articleTextArea.setRows(30);

		VerticalLayout articleOptionsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleOptionsLayout);
		articleOptionsLayout.addComponent(new Label("<h2>Nastavení článku</h2>", ContentMode.HTML));

		publicatedCheckBox.setCaption("Publikovat článek");
		publicatedCheckBox.setDescription("Je-li prázdné, uvidí článek pouze jeho autor");
		publicatedCheckBox.setImmediate(true);
		articleOptionsLayout.addComponent(publicatedCheckBox);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(new MarginInfo(true, false, false, false));
		editorTextLayout.addComponent(buttonLayout);

		// Náhled
		Button previewButton = new Button("Náhled");
		previewButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/document_16.png"));
		previewButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				String text = null;
				if (parts != null) {
					StringBuilder builder = new StringBuilder();
					builder.append(parts.getPrePart());
					builder.append(String.valueOf(articleTextArea.getValue()));
					builder.append(parts.getPostPart());
					text = builder.toString();
				} else {
					text = String.valueOf(articleTextArea.getValue());
				}

				ArticleDTO articleDTO = articleFacade.processPreview(text, getRequest().getContextRoot());

				PreviewWindow previewWindow = new PreviewWindow(articleDTO);
				getUI().addWindow(previewWindow);
			}

		});
		buttonLayout.addComponent(previewButton);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/save_16.png"));
		saveButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {
				if (isFormValid() == false)
					return;

				// pokud se bude měnit
				boolean oldMode = editMode;

				if (saveOrUpdateArticle()) {
					showInfo(oldMode ? "Úprava článku proběhla úspěšně" : "Uložení článku proběhlo úspěšně");
				} else {
					showWarning(oldMode ? "Úprava článku se nezdařila" : "Uložení článku se nezdařilo");
				}
			}

		});
		buttonLayout.addComponent(saveButton);
		saveButton.setClickShortcut(KeyCode.S, ModifierKey.CTRL);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/save_16.png"));
		saveAndCloseButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				if (isFormValid() == false)
					return;

				if (saveOrUpdateArticle()) {
					// Tady nemá cena dávat infowindow
					returnToArticle();
				} else {
					showWarning(editMode ? "Úprava článku se nezdařila" : "Uložení článku se nezdařilo");
				}

			}

		});
		buttonLayout.addComponent(saveAndCloseButton);

		// Zrušit
		Button cancelButton = new Button("Zrušit");
		cancelButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/delete_16.png"));
		cancelButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ConfirmWindow confirmSubwindow = new ConfirmWindow(
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

	@SuppressWarnings("unchecked")
	private Collection<String> getArticlesKeywords() {
		return (Collection<String>) articleKeywords.getValue();
	}

	private boolean saveOrUpdateArticle() {

		if (editMode) {

			String text = null;
			if (parts != null) {
				StringBuilder builder = new StringBuilder();
				builder.append(parts.getPrePart());
				builder.append(String.valueOf(articleTextArea.getValue()));
				builder.append(parts.getPostPart());
				text = builder.toString();
			} else {
				text = String.valueOf(articleTextArea.getValue());
			}

			return articleFacade.modifyArticle(String.valueOf(articleNameField.getValue()), text,
					getArticlesKeywords(), publicatedCheckBox.getValue(), article, getRequest().getContextRoot());
		} else {
			Long id = articleFacade.saveArticle(String.valueOf(articleNameField.getValue()),
					String.valueOf(articleTextArea.getValue()), getArticlesKeywords(), publicatedCheckBox.getValue(),
					category, getGrassUI().getUser(), getRequest().getContextRoot());

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
				URLIdentifierUtils.createURLIdentifier(article.getId(), article.getContentNode().getName())));
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToCategory() {
		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(categoryPageFactory,
				URLIdentifierUtils.createURLIdentifier(category.getId(), category.getName())));
	}

}

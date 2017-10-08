package cz.gattserver.grass3.articles.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.PluginServiceHolder;
import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.dto.ArticleDraftOverviewDTO;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.facade.ArticleFacade;
import cz.gattserver.grass3.articles.facade.ArticleProcessForm;
import cz.gattserver.grass3.articles.parser.PartsFinder;
import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.JScriptItem;
import cz.gattserver.grass3.pages.template.TwoColumnPage;
import cz.gattserver.grass3.template.ImageButton;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.URLPathAnalyzer;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.ui.TokenField;
import cz.gattserver.web.common.window.ConfirmWindow;

public class ArticlesEditorPage extends TwoColumnPage {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);

	@Autowired
	private NodeFacade nodeFacade;

	@Autowired
	private ArticleFacade articleFacade;

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private PluginServiceHolder pluginServiceHolder;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "articlesViewerPageFactory")
	private PageFactory articlesViewerPageFactory;

	private NodeBreadcrumbDTO node;

	private TextArea articleTextArea;
	private TokenField articleKeywords;
	private TextField articleNameField;
	private CheckBox publicatedCheckBox;

	private Long existingArticleId;
	private String existingArticleName;
	private Long existingDraftId;
	private Integer partNumber;

	private PartsFinder.Result parts;
	private Registration articleTextAreaFocusRegistration;

	public ArticlesEditorPage(GrassRequest request) {
		super(request);
		JavaScript.eval(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	private void defaultCreateContent(CustomLayout customlayout) {
		parts = null;
		ArticleDTO article = null;

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getNextPathToken();
		String identifierToken = analyzer.getNextPathToken();
		String partNumberToken = analyzer.getNextPathToken();
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
			node = nodeFacade.getNodeByIdForOverview(identifier.getId());
			articleNameField.setValue("");
			articleTextArea.setValue("");
			publicatedCheckBox.setValue(true);
		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
			article = articleFacade.getArticleForDetail(identifier.getId());
			node = article.getContentNode().getParent();
			existingArticleId = article.getId();
			existingArticleName = article.getContentNode().getName();

			articleNameField.setValue(article.getContentNode().getName());

			for (ContentTagDTO tagDTO : article.getContentNode().getContentTags()) {
				articleKeywords.addToken(new Token(tagDTO.getName()));
			}

			publicatedCheckBox.setValue(article.getContentNode().isPublicated());

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
				|| getGrassUI().getUser().isAdmin()) {
			super.createContent(customlayout);
		} else {
			// nemá oprávnění upravovat tento článek
			showError403();
			return;
		}
	}

	@Override
	protected void createContent(CustomLayout customlayout) {
		articleNameField = new TextField();
		articleKeywords = new TokenField();
		articleTextArea = new TextArea();
		publicatedCheckBox = new CheckBox();

		// zavádění listener pro JS listener akcí jako je vepsání tabulátoru
		articleTextAreaFocusRegistration = articleTextArea.addFocusListener(event -> {
			JavaScript.eval("registerTabListener()");
			// musí se odebrat, jinak budou problikávat vkládání přes
			// tlačítka
			articleTextAreaFocusRegistration.remove();
		});

		List<ArticleDraftOverviewDTO> drafts = articleFacade.getDraftsForUser(getGrassUI().getUser());

		if (drafts.isEmpty()) {
			// nejsou-li v DB žádné pro přihlášeného uživatele viditelné drafty
			// článků, otevři editor dle operace (new/edit)
			defaultCreateContent(customlayout);
		} else {
			// pokud jsou nalezeny drafty k dokončení, nabídni je k výběru
			UI.getCurrent().addWindow(new DraftMenuWindow(drafts) {
				private static final long serialVersionUID = 1040472008288522032L;

				@Override
				protected void onChoose(ArticleDraftOverviewDTO draft) {
					parts = null;
					ArticleDTO article = null;

					existingDraftId = draft.getId();

					node = draft.getContentNode().getParent();
					articleNameField.setValue(draft.getContentNode().getName());
					for (ContentTagDTO tagDTO : draft.getContentNode().getContentTags()) {
						articleKeywords.addToken(new Token(tagDTO.getName()));
					}
					publicatedCheckBox.setValue(draft.getContentNode().isPublicated());
					articleTextArea.setValue(draft.getText());

					// jedná se o draft již existujícího obsahu?
					if (draft.getContentNode().getDraftSourceId() != null) {
						article = articleFacade.getArticleForDetail(draft.getContentNode().getDraftSourceId());
						existingArticleId = article.getId();
						existingArticleName = article.getContentNode().getName();

						// Úprava části článku může být pouze u existujícího
						// článku
						if (draft.getPartNumber() != null) {
							partNumber = draft.getPartNumber();
							try {
								// parts se musí krájet z původního obsahu,
								// protože v draftu je teď jenom ta část
								parts = PartsFinder.findParts(
										new ByteArrayInputStream(article.getText().getBytes("UTF-8")), partNumber);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
								showError500();
								return;
							} catch (IOException e) {
								e.printStackTrace();
								showError500();
								return;
							}
						}
					}

					ArticlesEditorPage.super.createContent(customlayout);
				}

				@Override
				protected void onCancel() {
					// nebyl vybrán žádný draft, pokračuj výchozím otevřením
					// editoru (new/edit)
					defaultCreateContent(customlayout);
				}
			});
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
		Accordion accordion = new Accordion();
		accordion.setSizeFull();
		for (String group : groups) {

			CssLayout groupToolsLayout = new CssLayout();
			groupToolsLayout.addStyleName("tools_css_menu");
			accordion.addTab(groupToolsLayout, group);

			List<EditorButtonResources> resourcesBundles = new ArrayList<EditorButtonResources>(
					pluginServiceHolder.getGroupTags(group));
			Collections.sort(resourcesBundles);

			for (EditorButtonResources resourceBundle : resourcesBundles) {

				String prefix = resourceBundle.getPrefix();
				String suffix = resourceBundle.getSuffix();

				ImageButton btn = new ImageButton(resourceBundle.getDescription(), resourceBundle.getImage(), event -> {
					JavaScript.eval("insert('" + prefix + "','" + suffix + "')");
				});

				groupToolsLayout.addComponent(btn);
			}
		}

		// jQueryUI CSS
		loadCSS(getRequest().getContextRoot() + "/VAADIN/themes/grass/js/humanity/jquery-ui.css");

		// jQueryUI JS + jQueryUI Accordion render start
		loadJS(new JScriptItem[] { new JScriptItem("js/jquery-ui.js"),
				new JScriptItem("$( \"#accordion\" ).accordion({ event: \"click\", heightStyle: \"content\" })", true),
				new JScriptItem("$(\".ui-accordion-content\").css(\"padding\",\"1em 1em\")", true) });

		VerticalLayout hl = new VerticalLayout(accordion);
		hl.setMargin(true);

		return hl;
	}

	@Override
	protected Component createRightColumnContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout editorTextLayout = new VerticalLayout();
		editorTextLayout.setMargin(true);
		marginLayout.addComponent(editorTextLayout);

		// editor.js
		loadJS(new JScriptItem("articles/js/editor.js"));

		editorTextLayout.addComponent(new H2Label("Název článku"));
		editorTextLayout.addComponent(articleNameField);
		articleNameField.setWidth("100%");

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		articleKeywordsLayout.setMargin(false);
		editorTextLayout.addComponent(articleKeywordsLayout);

		// label
		articleKeywordsLayout.addComponent(new H2Label("Klíčová slova"));

		// menu tagů + textfield tagů
		HorizontalLayout keywordsMenuAndTextLayout = new HorizontalLayout();
		keywordsMenuAndTextLayout.setWidth("100%");
		keywordsMenuAndTextLayout.setSpacing(true);
		articleKeywordsLayout.addComponent(keywordsMenuAndTextLayout);

		keywordsMenuAndTextLayout.addComponent(articleKeywords);

		List<ContentTagDTO> contentTags = contentTagFacade.getContentTagsForOverview();
		contentTags.forEach(t -> {
			Token to = new Token(t.getName());
			articleKeywords.addTokenToInputField(to);
		});
		articleKeywords.isEnabled();
		articleKeywords.setAllowNewItems(true);
		articleKeywords.getInputField().setPlaceholder("klíčové slovo");

		editorTextLayout.addComponent(new H2Label("Obsah článku"));
		editorTextLayout.addComponent(articleTextArea);
		articleTextArea.setSizeFull();
		articleTextArea.setRows(30);

		editorTextLayout.addComponent(new H2Label("Nastavení článku"));
		publicatedCheckBox.setCaption("Publikovat článek");
		publicatedCheckBox.setDescription("Je-li prázdné, uvidí článek pouze jeho autor");
		editorTextLayout.addComponent(publicatedCheckBox);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(false);
		editorTextLayout.addComponent(buttonLayout);

		// Náhled
		Button previewButton = new Button("Náhled");
		previewButton.setIcon((com.vaadin.server.Resource) new ThemeResource(ImageIcons.DOCUMENT_16_ICON));
		previewButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {
				try {
					// Náhled ukazuje pouze danou část, která je upravovaná
					// (nespojuje parts)
					String draftName = articleNameField.getValue();
					Long id = articleFacade.saveArticle(draftName, articleTextArea.getValue(), getArticlesKeywords(),
							publicatedCheckBox.getValue(), node.getId(), getGrassUI().getUser().getId(),
							getRequest().getContextRoot(), ArticleProcessForm.PREVIEW, existingDraftId, partNumber,
							existingArticleId);

					if (id != null) {
						existingDraftId = id;
						JavaScript.eval("window.open('"
								+ getPageURL(articlesViewerPageFactory,
										URLIdentifierUtils.createURLIdentifier(existingDraftId, draftName))
								+ "','_blank');");
					}
				} catch (Exception e) {
					logger.error("Při ukládání náhledu článku došlo k chybě", e);
				}
			}

		});
		buttonLayout.addComponent(previewButton);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon((com.vaadin.server.Resource) new ThemeResource(ImageIcons.SAVE_16_ICON));
		saveButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {
				if (isFormValid() == false)
					return;

				if (saveOrUpdateArticle()) {
					showSilentInfo(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku proběhla úspěšně"
							: "Uložení článku proběhlo úspěšně");
				} else {
					showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila"
							: "Uložení článku se nezdařilo");
				}
			}

		});
		buttonLayout.addComponent(saveButton);
		saveButton.setClickShortcut(KeyCode.S, ModifierKey.CTRL);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon((com.vaadin.server.Resource) new ThemeResource(ImageIcons.SAVE_16_ICON));
		saveAndCloseButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {
				if (isFormValid() == false)
					return;

				if (saveOrUpdateArticle()) {
					// Tady nemá cena dávat infowindow
					returnToArticle();
				} else {
					showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila"
							: "Uložení článku se nezdařilo");
				}

			}

		});
		buttonLayout.addComponent(saveAndCloseButton);

		// Zrušit
		Button cancelButton = new Button("Zrušit");
		cancelButton.setIcon((com.vaadin.server.Resource) new ThemeResource(ImageIcons.DELETE_16_ICON));
		cancelButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ConfirmWindow confirmSubwindow = new ConfirmWindow(
						"Opravdu si přejete zavřít editor článku ? Veškeré neuložené změny budou ztraceny.", e -> {
							// ruším úpravu existujícího článku (vracím se na
							// článek), nebo nového (vracím se do kategorie) ?
							if (existingArticleId != null) {
								returnToArticle();
							} else {
								returnToNode();
							}
						});
				getUI().addWindow(confirmSubwindow);
			}

		});
		buttonLayout.addComponent(cancelButton);

		final Label autosaveLabel = new Label();
		buttonLayout.addComponent(autosaveLabel);

		// Auto-ukládání
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.articles.autosave", arguments -> {
			try {
				// Náhled ukazuje pouze danou část, která je upravovaná
				// (nespojuje parts)
				String draftName = articleNameField.getValue();
				Long id = articleFacade.saveArticle(draftName, articleTextArea.getValue(), getArticlesKeywords(),
						publicatedCheckBox.getValue(), node.getId(), getGrassUI().getUser().getId(),
						getRequest().getContextRoot(), ArticleProcessForm.DRAFT, existingDraftId, partNumber,
						existingArticleId);

				if (id != null) {
					existingDraftId = id;
					autosaveLabel.setValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
							+ " Automaticky uloženo");
					autosaveLabel.setStyleName("label-ok");
				}
			} catch (Exception e) {
				autosaveLabel.setValue(
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "Chyba uložení");
				autosaveLabel.setStyleName("label-err");
			}
		});
		JavaScript.eval("setInterval(function(){ cz.gattserver.grass3.articles.autosave(); }, 10000);");

		return marginLayout;
	}

	private boolean isFormValid() {

		String name = articleNameField.getValue();

		if (name == null || name.isEmpty()) {
			showWarning("Název článku nemůže být prázdný");
			return false;
		}

		return true;
	}

	private Collection<String> getArticlesKeywords() {
		List<String> tokens = new ArrayList<>();
		articleKeywords.getTokens().forEach(t -> tokens.add(t.getValue()));
		return tokens;
	}

	private boolean saveOrUpdateArticle() {
		try {
			String text = null;
			if (parts != null) {
				StringBuilder builder = new StringBuilder();
				builder.append(parts.getPrePart());
				builder.append(articleTextArea.getValue());
				builder.append(parts.getPostPart());
				text = builder.toString();
			} else {
				text = articleTextArea.getValue();
			}

			Long id = articleFacade.saveArticle(articleNameField.getValue(), text, getArticlesKeywords(),
					publicatedCheckBox.getValue(), node.getId(), getGrassUI().getUser().getId(),
					getRequest().getContextRoot(), ArticleProcessForm.FULL, this.existingArticleId);

			if (id != null) {
				// byl uložen článek, od teď eviduj draft, jako draft
				// existujícího obsahu
				this.existingArticleId = id;
				this.existingArticleName = articleNameField.getValue();
				return true;
			}
		} catch (Exception e) {
			logger.error("Při ukládání článku došlo k chybě", e);
		}
		return false;
	}

	/**
	 * Zavolá vrácení se na článek
	 */
	private void returnToArticle() {
		// smaž draft
		if (existingDraftId != null)
			articleFacade.deleteArticle(existingDraftId);

		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(articlesViewerPageFactory,
				URLIdentifierUtils.createURLIdentifier(existingArticleId, existingArticleName)));
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToNode() {
		// smaž draft
		if (existingDraftId != null)
			articleFacade.deleteArticle(existingDraftId);

		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
	}

	@Override
	public void attach() {
		super.attach();
		// aby se zaregistroval JS listener
		articleTextArea.focus();
	}

}

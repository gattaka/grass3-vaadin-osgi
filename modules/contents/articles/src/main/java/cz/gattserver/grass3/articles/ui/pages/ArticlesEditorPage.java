package cz.gattserver.grass3.articles.ui.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
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

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.util.PartsFinder;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.articles.ui.windows.DraftMenuWindow;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.components.ImageButton;
import cz.gattserver.grass3.ui.js.JScriptItem;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.TwoColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.server.URLPathAnalyzer;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;

public class ArticlesEditorPage extends TwoColumnPage {

	private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);

	@Autowired
	private NodeService nodeFacade;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private PluginRegisterService pluginRegister;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "articlesViewerPageFactory")
	private PageFactory articlesViewerPageFactory;

	private NodeOverviewTO node;

	private TextArea articleTextArea;
	private AdvancedTokenField articleKeywords;
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
		ArticleTO article = null;

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getNextPathToken();
		String identifierToken = analyzer.getNextPathToken();
		String partNumberToken = analyzer.getNextPathToken();
		if (operationToken == null || identifierToken == null) {
			logger.debug("Chybí operace nebo identifikátor cíle");
			throw new GrassPageException(404);
		}

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '" + identifierToken + "'");
			throw new GrassPageException(404);
		}

		// operace ?
		if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
			node = nodeFacade.getNodeByIdForOverview(identifier.getId());
			articleNameField.setValue("");
			articleTextArea.setValue("");
			publicatedCheckBox.setValue(true);
		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
			article = articleService.getArticleForDetail(identifier.getId());
			node = article.getContentNode().getParent();
			existingArticleId = article.getId();
			existingArticleName = article.getContentNode().getName();

			articleNameField.setValue(article.getContentNode().getName());

			for (ContentTagOverviewTO tagDTO : article.getContentNode().getContentTags()) {
				articleKeywords.addToken(new Token(tagDTO.getName()));
			}

			publicatedCheckBox.setValue(article.getContentNode().isPublicated());

			if (partNumberToken != null && (partNumber = Integer.valueOf(partNumberToken)) >= 0) {
				try {
					parts = PartsFinder.findParts(
							new ByteArrayInputStream(article.getText().getBytes(StandardCharsets.UTF_8)), partNumber);
				} catch (IOException e) {
					throw new GrassPageException(500, e);
				}
				articleTextArea.setValue(parts.getTargetPart());
			} else {
				articleTextArea.setValue(article.getText());
			}
		} else {
			logger.debug("Neznámá operace: '" + operationToken + "'");
			throw new GrassPageException(404);
		}

		if ((article == null || article.getContentNode().getAuthor().equals(UIUtils.getGrassUI().getUser()))
				|| UIUtils.getGrassUI().getUser().isAdmin()) {
			super.createContent(customlayout);
		} else {
			// nemá oprávnění upravovat tento článek
			throw new GrassPageException(403);
		}
	}

	@Override
	protected void createContent(CustomLayout customlayout) {
		articleNameField = new TextField();
		articleKeywords = new AdvancedTokenField();
		articleTextArea = new TextArea();
		publicatedCheckBox = new CheckBox();

		// zavádění listener pro JS listener akcí jako je vepsání tabulátoru
		articleTextAreaFocusRegistration = articleTextArea.addFocusListener(event -> {
			JavaScript.eval("registerTabListener()");
			// musí se odebrat, jinak budou problikávat vkládání přes
			// tlačítka
			articleTextAreaFocusRegistration.remove();
		});
		// aby se zaregistroval JS listener
		articleTextArea.focus();

		List<ArticleDraftOverviewTO> drafts = articleService.getDraftsForUser(UIUtils.getGrassUI().getUser().getId());

		if (drafts.isEmpty()) {
			// nejsou-li v DB žádné pro přihlášeného uživatele viditelné drafty
			// článků, otevři editor dle operace (new/edit)
			defaultCreateContent(customlayout);
		} else {
			// pokud jsou nalezeny drafty k dokončení, nabídni je k výběru
			UI.getCurrent().addWindow(new DraftMenuWindow(drafts) {
				private static final long serialVersionUID = 1040472008288522032L;

				@Override
				protected void onChoose(ArticleDraftOverviewTO draft) {
					parts = null;
					ArticleTO article = null;

					existingDraftId = draft.getId();

					node = draft.getContentNode().getParent();
					articleNameField.setValue(draft.getContentNode().getName());
					for (ContentTagOverviewTO tagDTO : draft.getContentNode().getContentTags()) {
						articleKeywords.addToken(new Token(tagDTO.getName()));
					}
					publicatedCheckBox.setValue(draft.getContentNode().isPublicated());
					articleTextArea.setValue(draft.getText());

					// jedná se o draft již existujícího obsahu?
					if (draft.getContentNode().getDraftSourceId() != null) {
						article = articleService.getArticleForDetail(draft.getContentNode().getDraftSourceId());
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
										new ByteArrayInputStream(article.getText().getBytes(StandardCharsets.UTF_8)),
										partNumber);
							} catch (IOException e) {
								throw new GrassPageException(500, e);
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
		List<String> groups = new ArrayList<String>(pluginRegister.getRegisteredGroups());
		Collections.sort(groups, (o1, o2) -> {
			if (o1 == null) {
				return o2 == null ? 0 : "".compareTo(o2);
			} else {
				if (o2 == null)
					return o1.compareTo(""); // druhý ber jako prázdný
				else
					return o1.compareTo(o2); // ani jeden není null
			}
		});

		// Projdi zaregistrované pluginy a vytvoř menu nástrojů
		Accordion accordion = new Accordion();
		accordion.setSizeFull();
		for (String group : groups) {
			CssLayout groupToolsLayout = new CssLayout();
			groupToolsLayout.addStyleName("tools_css_menu");
			accordion.addTab(groupToolsLayout, group);

			List<EditorButtonResourcesTO> resourcesBundles = new ArrayList<EditorButtonResourcesTO>(
					pluginRegister.getTagResourcesByGroup(group));
			Collections.sort(resourcesBundles);

			for (EditorButtonResourcesTO resourceBundle : resourcesBundles) {
				String prefix = resourceBundle.getPrefix();
				String suffix = resourceBundle.getSuffix();

				ImageButton btn = new ImageButton(resourceBundle.getDescription(), resourceBundle.getImage(), event -> {
					JavaScript.eval("insert('" + prefix + "','" + suffix + "')");
				});
				groupToolsLayout.addComponent(btn);
			}
		}

		// editor.js
		loadJS(new JScriptItem("articles/js/editor.js"));

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

		Set<ContentTagOverviewTO> contentTags = contentTagFacade.getTagsForOverviewOrderedByName();
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
		previewButton.setIcon(ImageIcon.DOCUMENT_16_ICON.createResource());
		previewButton.addClickListener(event -> {
			try {

				// Náhled ukazuje pouze danou část, která je upravovaná
				// (nespojuje parts)
				String draftName = articleNameField.getValue();
				ArticlePayloadTO payload = new ArticlePayloadTO(draftName, articleTextArea.getValue(),
						getArticlesKeywords(), publicatedCheckBox.getValue(), getRequest().getContextRoot());

				if (existingDraftId == null) {
					if (existingArticleId == null) {
						existingDraftId = articleService.saveDraft(payload, node.getId(),
								UIUtils.getGrassUI().getUser().getId(), true);
					} else {
						existingDraftId = articleService.saveDraftOfExistingArticle(payload, node.getId(),
								UIUtils.getGrassUI().getUser().getId(), partNumber, existingArticleId, true);
					}
				} else {
					if (existingArticleId == null) {
						articleService.modifyDraft(existingDraftId, payload, true);
					} else {
						articleService.modifyDraftOfExistingArticle(existingDraftId, payload, partNumber,
								existingArticleId, true);
					}
				}

				JavaScript
						.eval("window.open('"
								+ getPageURL(articlesViewerPageFactory,
										URLIdentifierUtils.createURLIdentifier(existingDraftId, draftName))
								+ "','_blank');");
			} catch (Exception e) {
				logger.error("Při ukládání náhledu článku došlo k chybě", e);
			}
		});
		buttonLayout.addComponent(previewButton);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon(ImageIcon.SAVE_16_ICON.createResource());
		saveButton.addClickListener(event -> {
			if (isFormValid() == false)
				return;
			if (saveOrUpdateArticle()) {
				UIUtils.showSilentInfo(ArticlesEditorPage.this.existingArticleId != null
						? "Úprava článku proběhla úspěšně" : "Uložení článku proběhlo úspěšně");
			} else {
				UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila"
						: "Uložení článku se nezdařilo");
			}
		});
		buttonLayout.addComponent(saveButton);
		saveButton.setClickShortcut(KeyCode.S, ModifierKey.CTRL);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon(ImageIcon.SAVE_16_ICON.createResource());
		saveAndCloseButton.addClickListener(event -> {
			if (isFormValid() == false)
				return;
			if (saveOrUpdateArticle()) {
				// Tady nemá cena dávat infowindow
				returnToArticle();
			} else {
				UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila"
						: "Uložení článku se nezdařilo");
			}
		});
		buttonLayout.addComponent(saveAndCloseButton);

		// Zrušit
		Button cancelButton = new Button("Zrušit");
		cancelButton.setIcon(ImageIcon.DELETE_16_ICON.createResource());
		cancelButton.addClickListener(event -> UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu si přejete zavřít editor článku ? Veškeré neuložené změny budou ztraceny.", e -> {
					// ruším úpravu existujícího článku (vracím se na
					// článek), nebo nového (vracím se do kategorie) ?
					if (existingArticleId != null) {
						returnToArticle();
					} else {
						returnToNode();
					}
				})));
		buttonLayout.addComponent(cancelButton);

		final Label autosaveLabel = new Label();
		buttonLayout.addComponent(autosaveLabel);

		// Auto-ukládání
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.articles.autosave", arguments -> {
			try {
				// Náhled ukazuje pouze danou část, která je upravovaná
				// (nespojuje parts)
				String draftName = articleNameField.getValue();
				ArticlePayloadTO payload = new ArticlePayloadTO(draftName, articleTextArea.getValue(),
						getArticlesKeywords(), publicatedCheckBox.getValue(), getRequest().getContextRoot());
				if (existingDraftId == null) {
					if (existingArticleId == null) {
						existingDraftId = articleService.saveDraft(payload, node.getId(),
								UIUtils.getGrassUI().getUser().getId(), false);
					} else {
						existingDraftId = articleService.saveDraftOfExistingArticle(payload, node.getId(),
								UIUtils.getGrassUI().getUser().getId(), partNumber, existingArticleId, false);
					}
				} else {
					if (existingArticleId == null) {
						articleService.modifyDraft(existingDraftId, payload, false);
					} else {
						articleService.modifyDraftOfExistingArticle(existingDraftId, payload, partNumber,
								existingArticleId, false);
					}
				}

				autosaveLabel.setValue(
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " Automaticky uloženo");
				autosaveLabel.setStyleName("label-ok");
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
			UIUtils.showWarning("Název článku nemůže být prázdný");
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

			ArticlePayloadTO payload = new ArticlePayloadTO(articleNameField.getValue(), text, getArticlesKeywords(),
					publicatedCheckBox.getValue(), getRequest().getContextRoot());
			if (existingArticleId == null) {
				// byl uložen článek, od teď eviduj draft, jako draft
				// existujícího obsahu
				existingArticleId = articleService.saveArticle(payload, node.getId(),
						UIUtils.getGrassUI().getUser().getId());
				this.existingArticleName = articleNameField.getValue();
			} else {
				articleService.modifyArticle(existingArticleId, payload, partNumber);
			}
			return true;
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
			articleService.deleteArticle(existingDraftId);

		JavaScript.eval("window.onbeforeunload = null;");
		UIUtils.redirect(getPageURL(articlesViewerPageFactory,
				URLIdentifierUtils.createURLIdentifier(existingArticleId, existingArticleName)));
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToNode() {
		// smaž draft
		if (existingDraftId != null)
			articleService.deleteArticle(existingDraftId);

		JavaScript.eval("window.onbeforeunload = null;");
		UIUtils.redirect(
				getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
	}

}

package cz.gattserver.grass3.articles.ui.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.shared.Registration;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.util.PartsFinder;
import cz.gattserver.grass3.articles.editor.parser.util.Result;
import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;
import cz.gattserver.grass3.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.articles.ui.windows.DraftMenuDialog;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.components.ImageButton;
import cz.gattserver.grass3.ui.js.JScriptItem;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.TwoColumnPage;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;

@Route("articles-editor")
public class ArticlesEditorPage extends TwoColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = -5107777679764121445L;

	private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);

	private static final String CLOSE_JS_DIV_ID = "close-js-div";

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private PluginRegisterService pluginRegister;

	@Resource(name = "articlesViewerPageFactory")
	private PageFactory articlesViewerPageFactory;

	private NodeOverviewTO node;

	private TextArea articleTextArea;
	private TokenField articleKeywords;
	private TextField articleNameField;
	private Checkbox publicatedCheckBox;

	private Long existingArticleId;
	private String existingArticleName;
	private Long existingDraftId;
	private Integer partNumber;

	private Result parts;
	private Registration articleTextAreaFocusRegistration;

	private String operationToken;
	private String identifierToken;
	private String partNumberToken;

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		if (chunks.length > 0)
			operationToken = chunks[0];
		if (chunks.length > 1)
			identifierToken = chunks[1];
		if (chunks.length > 2)
			partNumberToken = chunks[2];

		init();

		UI.getCurrent().getPage().executeJs(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	private void populateByExisting(ArticleTO article, String partNumberToken) {
		node = article.getContentNode().getParent();
		existingArticleId = article.getId();
		existingArticleName = article.getContentNode().getName();

		articleNameField.setValue(article.getContentNode().getName());

		for (ContentTagOverviewTO tagDTO : article.getContentNode().getContentTags())
			articleKeywords.addToken(tagDTO.getName());

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
	}

	private void checkAuthorization(ArticleTO article) {
		// má oprávnění upravovat tento článek?
		if (article != null && !article.getContentNode().getAuthor().equals(getUser()) && !getUser().isAdmin())
			throw new GrassPageException(403);
	}

	private void defaultCreateContent(Div customlayout) {
		parts = null;
		ArticleTO article = null;

		if (operationToken == null || identifierToken == null) {
			logger.debug("Chybí operace nebo identifikátor cíle");
			throw new GrassPageException(404);
		}

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: {}", identifierToken);
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
			populateByExisting(article, partNumberToken);
		} else {
			logger.debug("Neznámá operace: {}", operationToken);
			throw new GrassPageException(404);
		}

		checkAuthorization(article);
		super.createCenterElements(customlayout);
	}

	private void draftCreateContent(Div customlayout, List<ArticleDraftOverviewTO> drafts) {
		new DraftMenuDialog(drafts) {
			private static final long serialVersionUID = 1040472008288522032L;

			@Override
			protected void onChoose(ArticleDraftOverviewTO draft) {
				parts = null;
				ArticleTO article = null;

				existingDraftId = draft.getId();

				node = draft.getContentNode().getParent();
				articleNameField.setValue(draft.getContentNode().getName());
				for (ContentTagOverviewTO tagDTO : draft.getContentNode().getContentTags())
					articleKeywords.addToken(tagDTO.getName());
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

				ArticlesEditorPage.super.createCenterElements(customlayout);
			}

			@Override
			protected void onCancel() {
				// nebyl vybrán žádný draft, pokračuj výchozím otevřením
				// editoru (new/edit)
				defaultCreateContent(customlayout);
			}
		}.open();
	}

	@Override
	protected void createCenterElements(Div customlayout) {
		articleNameField = new TextField();
		FetchItemsCallback<String> fetchItemsCallback = (filter, offset, limit) -> contentTagFacade
				.findByFilter(filter, offset, limit).stream();
		SerializableFunction<String, Integer> serializableFunction = filter -> contentTagFacade.countByFilter(filter);
		articleKeywords = new TokenField(fetchItemsCallback, serializableFunction);
		articleKeywords.isEnabled();
		articleKeywords.setPlaceholder("klíčové slovo");

		articleTextArea = new TextArea();
		articleTextArea.setHeight("30em");
		articleTextArea.setWidthFull();
		publicatedCheckBox = new Checkbox();

		// editor.js
		loadJS(new JScriptItem("articles/js/editor.js"));

		// zavádění listener pro JS listener akcí jako je vepsání tabulátoru
		articleTextAreaFocusRegistration = articleTextArea.addFocusListener(event -> {
			UI.getCurrent().getPage().executeJs("registerTabListener()");
			// musí se odebrat, jinak budou problikávat vkládání přes
			// tlačítka
			articleTextAreaFocusRegistration.remove();
		});
		// aby se zaregistroval JS listener
		articleTextArea.focus();

		List<ArticleDraftOverviewTO> drafts = articleService.getDraftsForUser(getUser().getId());
		if (drafts.isEmpty()) {
			// nejsou-li v DB žádné pro přihlášeného uživatele viditelné drafty
			// článků, otevři editor dle operace (new/edit)
			defaultCreateContent(customlayout);
		} else {
			// pokud jsou nalezeny drafty k dokončení, nabídni je k výběru
			draftCreateContent(customlayout, drafts);
		}
	}

	@Override
	protected void createLeftColumnContent(Div layout) {
		List<String> groups = new ArrayList<>(pluginRegister.getRegisteredGroups());
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
		for (String group : groups) {
			Div groupToolsLayout = new Div();
			groupToolsLayout.addClassName("button-div");
			layout.add(new Details(group, groupToolsLayout));

			List<EditorButtonResourcesTO> resourcesBundles = new ArrayList<>(
					pluginRegister.getTagResourcesByGroup(group));
			Collections.sort(resourcesBundles);

			for (EditorButtonResourcesTO resourceBundle : resourcesBundles) {
				String prefix = resourceBundle.getPrefix();
				String suffix = resourceBundle.getSuffix();

				if (resourceBundle.getImage() != null) {
					ImageButton btn = new ImageButton(resourceBundle.getDescription(),
							new Image(resourceBundle.getImage(), resourceBundle.getTag()),
							event -> UI.getCurrent().getPage().executeJs("insert('" + prefix + "','" + suffix + "')"));
					btn.setTooltip(resourceBundle.getTag());
					groupToolsLayout.add(btn);
				} else {
					Button btn = new Button(resourceBundle.getDescription(),
							event -> UI.getCurrent().getPage().executeJs("insert('" + prefix + "','" + suffix + "')"));
					btn.getElement().setProperty("title", resourceBundle.getTag());
					groupToolsLayout.add(btn);
				}
			}
		}

		layout.getStyle().set("width", "420px").set("margin-left", "-200px");
	}

	private Button createPreviewButton() {
		Button previewButton = new ImageButton("Náhled", ImageIcon.DOCUMENT_16_ICON, event -> {
			try {

				// Náhled ukazuje pouze danou část, která je upravovaná
				// (nespojuje parts)
				String draftName = articleNameField.getValue();
				ArticlePayloadTO payload = new ArticlePayloadTO(draftName, articleTextArea.getValue(),
						articleKeywords.getValues(), publicatedCheckBox.getValue(), getContextPath());

				if (existingDraftId == null) {
					if (existingArticleId == null) {
						existingDraftId = articleService.saveDraft(payload, node.getId(), getUser().getId(), true);
					} else {
						existingDraftId = articleService.saveDraftOfExistingArticle(payload, node.getId(),
								getUser().getId(), partNumber, existingArticleId, true);
					}
				} else {
					if (existingArticleId == null) {
						articleService.modifyDraft(existingDraftId, payload, true);
					} else {
						articleService.modifyDraftOfExistingArticle(existingDraftId, payload, partNumber,
								existingArticleId, true);
					}
				}

				UI.getCurrent().getPage()
						.executeJs("window.open('"
								+ getPageURL(articlesViewerPageFactory,
										URLIdentifierUtils.createURLIdentifier(existingDraftId, draftName))
								+ "','_blank');");
			} catch (Exception e) {
				logger.error("Při ukládání náhledu článku došlo k chybě", e);
			}
		});
		return previewButton;
	}

	private Button createSaveButton() {
		Button saveButton = new ImageButton("Uložit", ImageIcon.SAVE_16_ICON, event -> {
			if (!isFormValid())
				return;
			if (saveOrUpdateArticle()) {
				UIUtils.showSilentInfo(ArticlesEditorPage.this.existingArticleId != null
						? "Úprava článku proběhla úspěšně" : "Uložení článku proběhlo úspěšně");
			} else {
				UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila"
						: "Uložení článku se nezdařilo");
			}
		});
		saveButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
		return saveButton;
	}

	private Button createSaveAndCloseButton() {
		Button saveAndCloseButton = new ImageButton("Uložit a zavřít", ImageIcon.SAVE_16_ICON, event -> {
			if (!isFormValid())
				return;
			if (saveOrUpdateArticle()) {
				// Tady nemá cena dávat infowindow
				returnToArticle();
			} else {
				UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila"
						: "Uložení článku se nezdařilo");
			}
		});
		return saveAndCloseButton;
	}

	private Button createCancelButton() {
		Button cancelButton = new ImageButton("Zrušit", ImageIcon.DELETE_16_ICON, event -> new ConfirmDialog(
				"Opravdu si přejete zavřít editor článku ? Veškeré neuložené změny budou ztraceny.", e -> {
					// ruším úpravu existujícího článku (vracím se na
					// článek), nebo nového (vracím se do kategorie) ?
					if (existingArticleId != null) {
						returnToArticle();
					} else {
						returnToNode();
					}
				}).open());
		return cancelButton;
	}

	private Span createAutosaveLabel() {
		final Span autosaveLabel = new Span();
		Div autosaveJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void autosaveCallback() {
				try {
					// Náhled ukazuje pouze danou část, která je upravovaná
					// (nespojuje parts)
					String draftName = articleNameField.getValue();
					ArticlePayloadTO payload = new ArticlePayloadTO(draftName, articleTextArea.getValue(),
							articleKeywords.getValues(), publicatedCheckBox.getValue(), getContextPath());
					if (existingDraftId == null) {
						if (existingArticleId == null) {
							existingDraftId = articleService.saveDraft(payload, node.getId(), getUser().getId(), false);
						} else {
							existingDraftId = articleService.saveDraftOfExistingArticle(payload, node.getId(),
									getUser().getId(), partNumber, existingArticleId, false);
						}
					} else {
						if (existingArticleId == null) {
							articleService.modifyDraft(existingDraftId, payload, false);
						} else {
							articleService.modifyDraftOfExistingArticle(existingDraftId, payload, partNumber,
									existingArticleId, false);
						}
					}

					autosaveLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
							+ " Automaticky uloženo");
					autosaveLabel.setClassName("label-ok");
				} catch (Exception e) {
					autosaveLabel.setText(
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "Chyba uložení");
					autosaveLabel.setClassName("label-err");
				}
			}
		};

		String autosaveJsDivId = "autosave-js-div";
		autosaveJsDiv.setId(autosaveJsDivId);
		add(autosaveJsDiv);

		UI.getCurrent().getPage().executeJs("setInterval(function(){ document.getElementById('" + autosaveJsDivId
				+ "').$server.autosaveCallback() }, 10000);");

		return autosaveLabel;
	}

	@Override
	protected void createRightColumnContent(Div layout) {
		layout.add(new H2("Název článku"));
		layout.add(articleNameField);
		articleNameField.setWidth("100%");

		// label
		layout.add(new H2("Klíčová slova"));

		// menu tagů + textfield tagů
		layout.add(articleKeywords);

		layout.add(new H2("Obsah článku"));
		layout.add(articleTextArea);

		layout.add(new H2("Nastavení článku"));
		publicatedCheckBox.setLabel("Publikovat článek");
		layout.add(publicatedCheckBox);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addClassName("top-margin");
		layout.add(buttonLayout);

		// Náhled
		Button previewButton = createPreviewButton();
		buttonLayout.add(previewButton);

		// Uložit
		Button saveButton = createSaveButton();
		buttonLayout.add(saveButton);

		// Uložit a zavřít
		Button saveAndCloseButton = createSaveAndCloseButton();
		buttonLayout.add(saveAndCloseButton);

		// Zrušit
		Button cancelButton = createCancelButton();
		buttonLayout.add(cancelButton);

		// Auto-ukládání
		Span autosaveLabel = createAutosaveLabel();
		buttonLayout.add(autosaveLabel);
	}

	private boolean isFormValid() {

		String name = articleNameField.getValue();

		if (name == null || name.isEmpty()) {
			UIUtils.showWarning("Název článku nemůže být prázdný");
			return false;
		}

		return true;
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

			ArticlePayloadTO payload = new ArticlePayloadTO(articleNameField.getValue(), text,
					articleKeywords.getValues(), publicatedCheckBox.getValue(), getContextPath());
			if (existingArticleId == null) {
				// byl uložen článek, od teď eviduj draft, jako draft
				// existujícího obsahu
				existingArticleId = articleService.saveArticle(payload, node.getId(), getUser().getId());
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

		Div closeJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void closeCallback() {
				UIUtils.redirect(getPageURL(articlesViewerPageFactory,
						URLIdentifierUtils.createURLIdentifier(existingArticleId, existingArticleName)));
			}
		};
		closeJsDiv.setId(CLOSE_JS_DIV_ID);
		add(closeJsDiv);

		UI.getCurrent().getPage()
				.executeJs("window.onbeforeunload = null; setTimeout(function(){ document.getElementById('"
						+ CLOSE_JS_DIV_ID + "').$server.closeCallback() }, 10);");
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToNode() {
		// smaž draft
		if (existingDraftId != null)
			articleService.deleteArticle(existingDraftId);

		Div closeJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void closeCallback() {
				UIUtils.redirect(getPageURL(nodePageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
			}
		};
		closeJsDiv.setId(CLOSE_JS_DIV_ID);
		add(closeJsDiv);

		UI.getCurrent().getPage()
				.executeJs("window.onbeforeunload = null; setTimeout(function(){ document.getElementById('"
						+ CLOSE_JS_DIV_ID + "').$server.closeCallback() }, 10);");
	}

}

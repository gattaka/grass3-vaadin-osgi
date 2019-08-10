package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.grass3.ui.windows.ProgressWindow;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.server.URLPathAnalyzer;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import elemental.json.JsonArray;
import net.engio.mbassy.listener.Handler;

public class PGEditorPage extends OneColumnPage {

	private static final Logger logger = LoggerFactory.getLogger(PGEditorPage.class);

	@Autowired
	private PGService pgService;

	@Autowired
	private ContentTagService contentTagFacade;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Autowired
	private EventBus eventBus;

	private UI ui = UI.getCurrent();
	private ProgressWindow progressIndicatorWindow;

	private NodeOverviewTO node;
	private PhotogalleryTO photogallery;

	private AdvancedTokenField photogalleryKeywords;
	private TextField photogalleryNameField;
	private DateTimeField photogalleryDateField;
	private CheckBox publicatedCheckBox;
	private CheckBox reprocessSlideshowAndMiniCheckBox;

	private String galleryDir;
	private boolean editMode;
	private boolean stayInEditor = false;

	/**
	 * Soubory, které byly nahrány od posledního uložení. V případě, že budou
	 * úpravy zrušeny, je potřeba tyto soubory smazat.
	 */
	private Set<PhotogalleryViewItemTO> newFiles = new HashSet<>();

	public PGEditorPage(GrassRequest request) {
		super(request);
		JavaScript.eval(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	@Override
	protected Layout createPayload() {

		photogalleryKeywords = new AdvancedTokenField();
		photogalleryNameField = new TextField();
		photogalleryDateField = new DateTimeField();
		publicatedCheckBox = new CheckBox();
		reprocessSlideshowAndMiniCheckBox = new CheckBox();

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getNextPathToken();
		String identifierToken = analyzer.getNextPathToken();
		if (operationToken == null || identifierToken == null) {
			logger.debug("Chybí operace nebo identifikátor cíle");
			throw new GrassPageException(404);
		}

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '{}'", identifierToken);
			throw new GrassPageException(404);
		}

		// operace ?
		if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
			editMode = false;
			node = nodeFacade.getNodeByIdForOverview(identifier.getId());
			photogalleryNameField.setValue("");
			publicatedCheckBox.setValue(true);
		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
			editMode = true;
			photogallery = pgService.getPhotogalleryForDetail(identifier.getId());

			if (photogallery == null)
				throw new GrassPageException(404);

			photogalleryNameField.setValue(photogallery.getContentNode().getName());
			for (ContentTagOverviewTO tagDTO : photogallery.getContentNode().getContentTags()) {
				photogalleryKeywords.addToken(new Token(tagDTO.getName()));
			}

			publicatedCheckBox.setValue(photogallery.getContentNode().isPublicated());
			photogalleryDateField.setValue(photogallery.getContentNode().getCreationDate());

			// nemá oprávnění upravovat tento obsah
			if (!photogallery.getContentNode().getAuthor().getName().equals(UIUtils.getGrassUI().getUser().getName())
					&& !UIUtils.getGrassUI().getUser().isAdmin())
				throw new GrassPageException(403);
		} else {
			logger.debug("Neznámá operace: '{}'", operationToken);
			throw new GrassPageException(404);
		}

		try {
			galleryDir = editMode ? photogallery.getPhotogalleryPath() : pgService.createGalleryDir();
		} catch (IOException e) {
			throw new GrassPageException(500);
		}

		return super.createPayload();
	}

	@Override
	protected Component createContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);
		marginLayout.setSpacing(false);

		VerticalLayout editorLayout = new VerticalLayout();
		editorLayout.setMargin(true);
		marginLayout.addComponent(editorLayout);

		editorLayout.addComponent(new H2Label("Název galerie"));
		editorLayout.addComponent(photogalleryNameField);
		photogalleryNameField.setWidth("100%");

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		articleKeywordsLayout.setMargin(false);
		editorLayout.addComponent(articleKeywordsLayout);

		// label
		articleKeywordsLayout.addComponent(new H2Label("Klíčová slova"));

		// menu tagů + textfield tagů
		HorizontalLayout keywordsMenuAndTextLayout = new HorizontalLayout();
		keywordsMenuAndTextLayout.setWidth("100%");
		keywordsMenuAndTextLayout.setSpacing(true);
		articleKeywordsLayout.addComponent(keywordsMenuAndTextLayout);

		keywordsMenuAndTextLayout.addComponent(photogalleryKeywords);

		Set<ContentTagOverviewTO> contentTags = contentTagFacade.getTagsForOverviewOrderedByName();
		contentTags.forEach(t -> {
			Token to = new Token(t.getName());
			photogalleryKeywords.addTokenToInputField(to);
		});
		photogalleryKeywords.isEnabled();
		photogalleryKeywords.setAllowNewItems(true);
		photogalleryKeywords.getInputField().setPlaceholder("klíčové slovo");

		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setSpacing(true);
		contentLayout.setMargin(false);
		editorLayout.addComponent(contentLayout);
		contentLayout.addComponent(new H2Label("Položky"));

		HorizontalLayout gridLayout = new HorizontalLayout();
		gridLayout.setMargin(false);
		gridLayout.setSpacing(true);
		gridLayout.setWidth("100%");
		contentLayout.addComponent(gridLayout);

		final VerticalLayout imageWrapper = new VerticalLayout();
		imageWrapper.setWidth("300px");
		imageWrapper.setHeight("300px");
		imageWrapper.addStyleName("bordered");

		final Embedded image = new Embedded();
		image.addStyleName("resized-preview");

		final Label previewLabel = new Label("<center>Náhled</center>", ContentMode.HTML);
		imageWrapper.addComponent(previewLabel);
		imageWrapper.setComponentAlignment(previewLabel, Alignment.MIDDLE_CENTER);

		final Grid<PhotogalleryViewItemTO> grid = new Grid<>(PhotogalleryViewItemTO.class);
		final List<PhotogalleryViewItemTO> items;
		if (editMode) {
			try {
				items = pgService.getItems(galleryDir);
			} catch (IOException e) {
				throw new GrassPageException(500, e);
			}
		} else {
			items = new ArrayList<>();
		}
		grid.setItems(items);
		grid.setColumns("name");
		grid.getColumn("name").setCaption("Název");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.addItemClickListener(e -> {
			PhotogalleryViewItemTO item = e.getItem();
			if (item == null)
				return;
			if (e.getMouseEventDetails().isShiftKey()) {
				if (grid.getSelectedItems().contains(item))
					grid.deselect(item);
				else
					grid.select(item);
			} else {
				if (grid.getSelectedItems().size() == 1 && grid.getSelectedItems().iterator().next().equals(item)) {
					grid.deselect(item);
				} else {
					grid.deselectAll();
					grid.select(item);
				}
			}
		});
		grid.setSizeFull();

		final Button removeBtn = new DeleteGridButton<>("Odstranit", selected -> {
			List<PhotogalleryViewItemTO> removed = pgService.deleteFiles(selected, galleryDir);
			if (removed.size() != selected.size())
				UIUtils.showWarning("Nezdařilo se smazat některé soubory");
			items.removeAll(removed);
			grid.getDataProvider().refreshAll();
		}, grid);

		grid.addSelectionListener(event -> {
			imageWrapper.removeAllComponents();
			if (!event.getAllSelectedItems().isEmpty() && event.getAllSelectedItems().size() == 1) {
				PhotogalleryViewItemTO itemTO = event.getFirstSelectedItem().get();
				String file = itemTO.getName();
				if (PGUtils.isImage(file)) {
					try {
						image.setSource(new FileResource(pgService.getFullImage(galleryDir, file).toFile()));
						imageWrapper.addComponent(image);
						return;
					} catch (Exception e) {
						UIUtils.showWarning("Obrázek nelze zobrazit");
					}
				}
				removeBtn.setEnabled(true);
			}
			imageWrapper.addComponent(previewLabel);
			imageWrapper.setComponentAlignment(previewLabel, Alignment.MIDDLE_CENTER);
		});
		gridLayout.addComponent(grid);
		gridLayout.setExpandRatio(grid, 1);

		gridLayout.addComponent(imageWrapper);

		HorizontalLayout itemsButtonLayout = new HorizontalLayout();
		itemsButtonLayout.setSpacing(true);
		itemsButtonLayout.setMargin(false);
		contentLayout.addComponent(itemsButtonLayout);

		itemsButtonLayout.addComponent(createUploadButton(grid, items));
		itemsButtonLayout.addComponent(removeBtn);

		VerticalLayout contentOptionsLayout = new VerticalLayout();
		contentOptionsLayout.setSpacing(true);
		contentOptionsLayout.setMargin(false);
		editorLayout.addComponent(contentOptionsLayout);
		contentOptionsLayout.addComponent(new H2Label("Nastavení"));

		publicatedCheckBox.setCaption("Publikovat galerii");
		publicatedCheckBox.setDescription("Je-li prázdné, uvidí galerii pouze její autor");
		contentOptionsLayout.addComponent(publicatedCheckBox);

		reprocessSlideshowAndMiniCheckBox.setCaption("Přegenerovat slideshow a miniatury");
		contentOptionsLayout.addComponent(reprocessSlideshowAndMiniCheckBox);

		photogalleryDateField.setCaption("Přepsat datum vytvoření galerie");
		contentOptionsLayout.addComponent(photogalleryDateField);

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.setMargin(false);
		editorLayout.addComponent(buttonsLayout);

		populateButtonsLayout(buttonsLayout);

		return marginLayout;
	}

	private MultiUpload createUploadButton(final Grid<PhotogalleryViewItemTO> grid,
			final List<PhotogalleryViewItemTO> items) {
		return new PGMultiUpload(galleryDir) {
			private static final long serialVersionUID = -693498481160129134L;

			@Override
			protected void fileUploadSuccess(String fileName) {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				itemTO.setName(fileName);
				newFiles.add(itemTO);
				items.add(itemTO);
				grid.setItems(items);
			}

		};
	}

	private void populateButtonsLayout(HorizontalLayout buttonLayout) {

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon(ImageIcon.SAVE_16_ICON.createResource());
		saveButton.addClickListener(event -> {
			if (!isFormValid())
				return;
			stayInEditor = true;
			saveOrUpdatePhotogallery();
		});
		buttonLayout.addComponent(saveButton);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon(ImageIcon.SAVE_16_ICON.createResource());
		saveAndCloseButton.addClickListener(event -> {
			if (!isFormValid())
				return;
			stayInEditor = false;
			saveOrUpdatePhotogallery();
		});
		buttonLayout.addComponent(saveAndCloseButton);

		// Zrušit
		Button cancelButton = new Button("Zrušit");
		cancelButton.setIcon(ImageIcon.DELETE_16_ICON.createResource());
		cancelButton.addClickListener(event -> UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu si přejete zavřít editor galerie ? Veškeré neuložené změny budou ztraceny.", e -> {
					cleanAfterCancelEdit();
					if (editMode)
						returnToPhotogallery();
					else
						returnToNode();
				})));
		buttonLayout.addComponent(cancelButton);
	}

	private void cleanAfterCancelEdit() {
		if (editMode) {
			pgService.deleteFiles(newFiles, galleryDir);
		} else {
			try {
				pgService.deleteDraftGallery(galleryDir);
			} catch (Exception e) {
				logger.error("Nezdařilo se smazat zrušenou rozpracovanou galerii", e);
				throw new GrassPageException(500, e);
			}
		}
	}

	private boolean isFormValid() {
		String name = photogalleryNameField.getValue();
		if (name == null || name.isEmpty()) {
			UIUtils.showWarning("Název galerie nemůže být prázdný");
			return false;
		}
		return true;
	}

	private void saveOrUpdatePhotogallery() {
		logger.info("saveOrUpdatePhotogallery thread: " + Thread.currentThread().getId());
		List<String> tokens = new ArrayList<>();
		photogalleryKeywords.getTokens().forEach(t -> tokens.add(t.getValue()));
		PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogalleryNameField.getValue(), galleryDir,
				tokens, publicatedCheckBox.getValue(), reprocessSlideshowAndMiniCheckBox.getValue());

		eventBus.subscribe(PGEditorPage.this);
		progressIndicatorWindow = new ProgressWindow();

		if (editMode) {
			pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO,
					photogalleryDateField.getValue());
		} else {
			pgService.savePhotogallery(UUID.randomUUID(), payloadTO, node.getId(),
					UIUtils.getGrassUI().getUser().getId(), photogalleryDateField.getValue());
		}
	}

	/**
	 * Zavolá vrácení se na galerii
	 */
	private void returnToPhotogallery() {
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.closecallback", new JavaScriptFunction() {
			private static final long serialVersionUID = 5850638851716815161L;

			@Override
			public void call(JsonArray arguments) {
				UIUtils.redirect(getPageURL(photogalleryViewerPageFactory, URLIdentifierUtils
						.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
			}
		});
		JavaScript.eval(
				"window.onbeforeunload = null; setTimeout(function(){ cz.gattserver.grass3.closecallback(); }, 10);");
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToNode() {
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.closecallback", new JavaScriptFunction() {
			private static final long serialVersionUID = 5850638851716815161L;

			@Override
			public void call(JsonArray arguments) {
				UIUtils.redirect(getPageURL(nodePageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
			}
		});
		JavaScript.eval(
				"window.onbeforeunload = null; setTimeout(function(){ cz.gattserver.grass3.closecallback(); }, 10);");
	}

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			ui.addWindow(progressIndicatorWindow);
		});
	}

	@Handler
	protected void onProcessProgress(PGProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();
			if (editMode)
				onModifyResult(event);
			else
				onSaveResult(event);
		});
		eventBus.unsubscribe(PGEditorPage.this);
	}

	private void onSaveResult(PGProcessResultEvent event) {
		Long id = event.getGalleryId();
		if (event.isSuccess() && id != null) {
			photogallery = pgService.getPhotogalleryForDetail(id);
			// soubory byly uloženy a nepodléhají
			// podmíněnému smazání
			newFiles.clear();
			if (!stayInEditor)
				returnToPhotogallery();
			// odteď budeme editovat
			editMode = true;
			UIUtils.showInfo("Uložení galerie proběhlo úspěšně");
		} else {
			UIUtils.showWarning("Uložení galerie se nezdařilo");
		}
	}

	private void onModifyResult(PGProcessResultEvent event) {
		if (event.isSuccess()) {
			// soubory byly uloženy a nepodléhají
			// podmíněnému smazání
			newFiles.clear();
			if (!stayInEditor)
				returnToPhotogallery();
			UIUtils.showInfo("Úprava galerie proběhla úspěšně");
		} else {
			UIUtils.showWarning("Úprava galerie se nezdařila");
		}
	}
}

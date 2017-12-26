package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
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
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.ui.components.BaseProgressBar;
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
import cz.gattserver.web.common.ui.window.WarnWindow;
import net.engio.mbassy.listener.Handler;

public class PGEditorPage extends OneColumnPage {

	private static final Logger logger = LoggerFactory.getLogger(PGEditorPage.class);

	@Autowired
	private NodeService nodeFacade;

	@Autowired
	private PGService pgService;

	@Autowired
	private ContentTagService contentTagFacade;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "photogalleryViewerPageFactory")
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

	private Path galleryDir;

	private boolean editMode;

	private List<Path> newFiles;

	private boolean stayInEditor = false;

	private boolean warnWindowDeployed = false;
	private Label existingFiles;
	private WarnWindow warnWindow;

	public PGEditorPage(GrassRequest request) {
		super(request);
		JavaScript.eval(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	@Override
	protected Layout createPayload() {

		newFiles = new ArrayList<>();

		photogalleryKeywords = new AdvancedTokenField();
		photogalleryNameField = new TextField();
		photogalleryDateField = new DateTimeField();
		publicatedCheckBox = new CheckBox();

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getNextPathToken();
		String identifierToken = analyzer.getNextPathToken();
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
			editMode = false;
			node = nodeFacade.getNodeByIdForOverview(identifier.getId());
			photogalleryNameField.setValue("");
			publicatedCheckBox.setValue(true);
		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
			editMode = true;
			photogallery = pgService.getPhotogalleryForDetail(identifier.getId());
			photogalleryNameField.setValue(photogallery.getContentNode().getName());

			for (ContentTagOverviewTO tagDTO : photogallery.getContentNode().getContentTags()) {
				photogalleryKeywords.addToken(new Token(tagDTO.getName()));
			}

			publicatedCheckBox.setValue(photogallery.getContentNode().isPublicated());
			photogalleryDateField.setValue(photogallery.getContentNode().getCreationDate());
		} else {
			logger.debug("Neznámá operace: '" + operationToken + "'");
			throw new GrassPageException(404);
		}

		if ((photogallery == null || photogallery.getContentNode().getAuthor().equals(UIUtils.getGrassUI().getUser()))
				|| UIUtils.getGrassUI().getUser().isAdmin()) {
		} else {
			// nemá oprávnění upravovat tento obsah
			throw new GrassPageException(403);
		}

		return super.createPayload();
	}

	@Override
	protected Component createContent() {

		final Path dir = editMode ? pgService.getGalleryDir(photogallery) : pgService.createGalleryDir();
		galleryDir = dir;

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

		final GridLayout gridLayout = new GridLayout(3, 2);
		gridLayout.setSpacing(true);
		gridLayout.setColumnExpandRatio(2, 1);
		gridLayout.setWidth("100%");
		contentLayout.addComponent(gridLayout);

		final Embedded image = new Embedded();
		image.setWidth("300px");

		final Label previewLabel = new Label("<center>Náhled</center>", ContentMode.HTML);
		previewLabel.setHeight("300px");
		previewLabel.setWidth("300px");
		previewLabel.addStyleName("bordered");
		gridLayout.addComponent(previewLabel, 0, 0, 1, 0);

		final VerticalLayout imageWrapper = new VerticalLayout();
		imageWrapper.setWidth("300px");
		imageWrapper.setHeight("300px");
		imageWrapper.addComponent(image);
		imageWrapper.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
		// gridLayout.addComponent(imageWrapper, 0, 0, 1, 0);

		final Grid<Path> grid = new Grid<>();
		final List<Path> items = new ArrayList<>();
		if (editMode) {
			try (Stream<Path> stream = Files.list(galleryDir)) {
				stream.filter(file -> !Files.isDirectory(file)).forEach(items::add);
			} catch (IOException e) {
				throw new GrassPageException(500, e);
			}
		}
		grid.setItems(items);

		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setSizeFull();
		grid.getColumn("name").setCaption("Název");
		grid.setColumns("name");

		final Button removeBtn = new DeleteGridButton<>("Odstranit", files -> files.forEach(file -> {
			if (editMode) {
				if (PGUtils.isImage(file)) {
					pgService.tryDeleteMiniatureImage(file.getFileName().toString(), photogallery);
					pgService.tryDeleteSlideshowImage(file.getFileName().toString(), photogallery);
				}
				if (PGUtils.isVideo(file))
					pgService.tryDeletePreviewImage(file.getFileName().toString(), photogallery);
			}
			try {
				Files.delete(file);
				items.remove(file);
			} catch (IOException e) {

			}
			grid.getDataProvider().refreshAll();
		}), grid);

		gridLayout.addComponent(removeBtn, 1, 1);
		gridLayout.setComponentAlignment(removeBtn, Alignment.MIDDLE_CENTER);

		grid.addSelectionListener(event -> {
			if (event.getAllSelectedItems().isEmpty()) {
				gridLayout.removeComponent(imageWrapper);
				gridLayout.addComponent(previewLabel, 0, 0, 1, 0);
			} else {
				Path file = event.getFirstSelectedItem().get();
				if (PGUtils.isImage(file)) {
					if (imageWrapper.getParent() == null) {
						gridLayout.removeComponent(previewLabel);
						gridLayout.addComponent(imageWrapper, 0, 0, 1, 0);
					}
					image.setSource(new FileResource(file.toFile()));
				}
				removeBtn.setEnabled(true);
			}
		});
		gridLayout.addComponent(grid, 2, 0, 2, 1);

		VerticalLayout uploadWrapper = new VerticalLayout();
		uploadWrapper.setSpacing(true);
		uploadWrapper.setWidth("100%");
		uploadWrapper.setMargin(true);
		uploadWrapper.addStyleName("bordered");
		contentLayout.addComponent(uploadWrapper);

		MultiUpload multiUpload = new MultiUpload() {
			private static final long serialVersionUID = -5223991901495532219L;

			@Override
			protected void handleFile(InputStream in, String fileName, String mimeType, long length) {
				Path path = dir.resolve(fileName);
				try {
					Files.copy(in, path);
					newFiles.add(path);
					items.add(path);
					grid.getDataProvider().refreshAll();
				} catch (FileAlreadyExistsException f) {
					if (warnWindowDeployed == false) {
						existingFiles = new Label("", ContentMode.HTML);
						warnWindow = new WarnWindow("Následující soubory již existují:") {
							private static final long serialVersionUID = 3428203680996794639L;

							@Override
							protected void createDetails(String details) {
								addComponent(existingFiles);
							}

							public void close() {
								existingFiles.setValue("");
								warnWindowDeployed = false;
								super.close();
							}
						};
						UI.getCurrent().addWindow(warnWindow);
						warnWindowDeployed = true;
					}
					existingFiles.setValue(existingFiles.getValue() + fileName + "<br/>");
				} catch (IOException e) {
					logger.error("Nezdařilo se uložit soubor {}", fileName, e);
				}
			}

		};
		multiUpload.setCaption("Nahrát obsah");
		uploadWrapper.addComponent(multiUpload);
		uploadWrapper.setComponentAlignment(multiUpload, Alignment.MIDDLE_CENTER);

		VerticalLayout contentOptionsLayout = new VerticalLayout();
		contentOptionsLayout.setSpacing(true);
		contentOptionsLayout.setMargin(false);
		editorLayout.addComponent(contentOptionsLayout);
		contentOptionsLayout.addComponent(new H2Label("Nastavení"));

		publicatedCheckBox.setCaption("Publikovat galerii");
		publicatedCheckBox.setDescription("Je-li prázdné, uvidí galerii pouze její autor");
		contentOptionsLayout.addComponent(publicatedCheckBox);

		photogalleryDateField.setCaption("Přepsat datum vytvoření galerie");
		contentOptionsLayout.addComponent(photogalleryDateField);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(false);
		editorLayout.addComponent(buttonLayout);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon(ImageIcon.SAVE_16_ICON.createResource());
		saveButton.addClickListener(event -> {
			if (isFormValid() == false)
				return;
			stayInEditor = true;
			saveOrUpdatePhotogallery();
		});
		buttonLayout.addComponent(saveButton);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon(ImageIcon.SAVE_16_ICON.createResource());
		saveAndCloseButton.addClickListener(event -> {
			if (isFormValid() == false)
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
					// ruším úpravu existující galerie (vracím se na
					// galerii), nebo nové (vracím se do kategorie) ?
					if (editMode) {
						returnToPhotogallery();
					} else {
						returnToNode();
					}
				})));
		buttonLayout.addComponent(cancelButton);

		return marginLayout;
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
		eventBus.subscribe(PGEditorPage.this);
		ui.setPollInterval(200);
		List<String> tokens = new ArrayList<>();
		photogalleryKeywords.getTokens().forEach(t -> tokens.add(t.getValue()));
		PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogalleryNameField.getValue(), galleryDir,
				tokens, publicatedCheckBox.getValue());

		if (editMode) {
			pgService.modifyPhotogallery(photogallery.getId(), payloadTO, photogalleryDateField.getValue());
		} else {
			pgService.savePhotogallery(payloadTO, node.getId(), UIUtils.getGrassUI().getUser().getId(),
					photogalleryDateField.getValue());
		}
	}

	/**
	 * Zavolá vrácení se na galerii
	 */
	private void returnToPhotogallery() {
		JavaScript.eval("window.onbeforeunload = null;");
		UIUtils.redirect(getPageURL(photogalleryViewerPageFactory,
				URLIdentifierUtils.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToNode() {
		JavaScript.eval("window.onbeforeunload = null;");
		UIUtils.redirect(
				getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
	}

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		ui.access(() -> {
			BaseProgressBar progressBar = new BaseProgressBar(event.getCountOfStepsToDo());
			progressBar.setIndeterminate(false);
			progressBar.setValue(0f);
			progressIndicatorWindow = new ProgressWindow(progressBar);
			ui.addWindow(progressIndicatorWindow);
		});
	}

	@Handler
	protected void onProcessProgress(PGProcessProgressEvent event) {
		ui.access(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGProcessResultEvent event) {
		ui.access(() -> {
			// ui.setPollInterval(-1);
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.closeOnDone();

			Long id = event.getGalleryId();
			if (event.isSuccess() && (id != null || editMode)) {
				if (editMode == false)
					photogallery = pgService.getPhotogalleryForDetail(id);

				// soubory byly uloženy a nepodléhají
				// podmíněnému smazání
				newFiles.clear();
				if (stayInEditor == false)
					returnToPhotogallery();

				UIUtils.showInfo(editMode ? "Úprava galerie proběhla úspěšně" : "Uložení galerie proběhlo úspěšně");
			} else {
				UIUtils.showWarning(editMode ? "Úprava galerie se nezdařila" : "Uložení galerie se nezdařilo");
			}

			// odteď budeme editovat
			editMode = true;
		});
		eventBus.unsubscribe(PGEditorPage.this);
	}
}

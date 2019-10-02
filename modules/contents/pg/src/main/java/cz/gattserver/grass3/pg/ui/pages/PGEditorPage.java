package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.StreamResource;

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
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.components.button.CloseButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.grass3.ui.dialogs.ProgressDialog;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import net.engio.mbassy.listener.Handler;

@Route("pg-editor")
public class PGEditorPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = 8685208356478891386L;

	private static final Logger logger = LoggerFactory.getLogger(PGEditorPage.class);

	private static final String CLOSE_JS_DIV_ID = "close-js-div";

	@Autowired
	private PGService pgService;

	@Autowired
	private ContentTagService contentTagFacade;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Autowired
	private EventBus eventBus;

	private ProgressDialog progressIndicatorWindow;

	private NodeOverviewTO node;
	private PhotogalleryTO photogallery;

	private TokenField photogalleryKeywords;
	private TextField photogalleryNameField;
	private DatePicker photogalleryDateField;
	private Checkbox publicatedCheckBox;
	private Checkbox reprocessSlideshowAndMiniCheckBox;

	private String galleryDir;
	private boolean editMode;
	private boolean stayInEditor = false;

	/**
	 * Soubory, které byly nahrány od posledního uložení. V případě, že budou
	 * úpravy zrušeny, je potřeba tyto soubory smazat.
	 */
	private Set<PhotogalleryViewItemTO> newFiles = new HashSet<>();

	private String operationToken;
	private String identifierToken;

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		if (chunks.length > 0)
			operationToken = chunks[0];
		if (chunks.length > 1)
			identifierToken = chunks[1];

		init();

		UI.getCurrent().getPage().executeJs(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	@Override
	protected void createColumnContent(Div editorLayout) {
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '{}'", identifierToken);
			throw new GrassPageException(404);
		}

		FetchItemsCallback<String> fetchItemsCallback = (filter, offset, limit) -> contentTagFacade
				.findByFilter(filter, offset, limit).stream();
		SerializableFunction<String, Integer> serializableFunction = filter -> contentTagFacade.countByFilter(filter);
		photogalleryKeywords = new TokenField(fetchItemsCallback, serializableFunction);

		photogalleryNameField = new TextField();
		photogalleryDateField = new DatePicker();
		publicatedCheckBox = new Checkbox();
		reprocessSlideshowAndMiniCheckBox = new Checkbox();

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
			for (ContentTagOverviewTO tagDTO : photogallery.getContentNode().getContentTags())
				photogalleryKeywords.addToken(tagDTO.getName());

			publicatedCheckBox.setValue(photogallery.getContentNode().isPublicated());
			photogalleryDateField.setValue(photogallery.getContentNode().getCreationDate().toLocalDate());

			// nemá oprávnění upravovat tento obsah
			if (!photogallery.getContentNode().getAuthor().getName().equals(getUser().getName())
					&& !getUser().isAdmin())
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

		editorLayout.add(new H2("Název galerie"));
		editorLayout.add(photogalleryNameField);
		photogalleryNameField.setWidth("100%");

		// label
		editorLayout.add(new H2("Klíčová slova"));

		// menu tagů + textfield tagů
		editorLayout.add(photogalleryKeywords);

		photogalleryKeywords.isEnabled();
		photogalleryKeywords.setAllowNewItems(true);
		photogalleryKeywords.getInputField().setPlaceholder("klíčové slovo");

		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setSpacing(true);
		contentLayout.setPadding(false);
		editorLayout.add(contentLayout);
		contentLayout.add(new H2("Položky"));

		HorizontalLayout gridLayout = new HorizontalLayout();
		gridLayout.setPadding(false);
		gridLayout.setSpacing(true);
		gridLayout.setWidth("100%");
		contentLayout.add(gridLayout);

		final VerticalLayout imageWrapper = new VerticalLayout();
		imageWrapper.setWidth("300px");
		imageWrapper.setHeight("300px");
		imageWrapper.addClassName("bordered");

		final Image image = new Image();
		image.addClassName("resized-preview");

		final HtmlDiv previewLabel = new HtmlDiv("<center>Náhled</center>");
		imageWrapper.add(previewLabel);

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
		grid.getColumnByKey("name").setHeader("Název");
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.addItemClickListener(e -> {
			PhotogalleryViewItemTO item = e.getItem();
			if (item == null)
				return;
			if (e.isShiftKey()) {
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

		final DeleteGridButton<PhotogalleryViewItemTO> removeBtn = new DeleteGridButton<>("Odstranit", selected -> {
			List<PhotogalleryViewItemTO> removed = pgService.deleteFiles(selected, galleryDir);
			if (removed.size() != selected.size())
				UIUtils.showWarning("Nezdařilo se smazat některé soubory");
			items.removeAll(removed);
			grid.getDataProvider().refreshAll();
		}, grid);

		grid.addSelectionListener(event -> {
			imageWrapper.removeAll();
			if (!event.getAllSelectedItems().isEmpty() && event.getAllSelectedItems().size() == 1) {
				PhotogalleryViewItemTO itemTO = event.getFirstSelectedItem().get();
				String file = itemTO.getName();
				if (PGUtils.isImage(file)) {
					image.setSrc(new StreamResource(file, () -> {
						try {
							return Files.newInputStream(pgService.getFullImage(galleryDir, file));
						} catch (IOException e1) {
							UIUtils.showWarning("Obrázek nelze zobrazit");
							return null;
						}
					}));
					imageWrapper.add(image);
					return;
				}
				removeBtn.setEnabled(true);
			}
			imageWrapper.add(previewLabel);
			imageWrapper.setAlignItems(Alignment.CENTER);
		});
		gridLayout.add(grid);
		gridLayout.add(imageWrapper);

		HorizontalLayout itemsButtonLayout = new HorizontalLayout();
		itemsButtonLayout.setSpacing(true);
		itemsButtonLayout.setPadding(false);
		contentLayout.add(itemsButtonLayout);

		PGMultiUpload upload = new PGMultiUpload(galleryDir) {
			private static final long serialVersionUID = 8317049226635860025L;

			@Override
			protected void fileUploadSuccess(String fileName) {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				itemTO.setName(fileName);
				newFiles.add(itemTO);
				items.add(itemTO);
				grid.setItems(items);
			}
		};
		upload.addClassName("top-margin");
		itemsButtonLayout.add(upload);
		itemsButtonLayout.add(removeBtn);

		VerticalLayout contentOptionsLayout = new VerticalLayout();
		contentOptionsLayout.setSpacing(true);
		contentOptionsLayout.setPadding(false);
		editorLayout.add(contentOptionsLayout);
		contentOptionsLayout.add(new H2("Nastavení"));

		publicatedCheckBox.setLabel("Publikovat galerii");
		contentOptionsLayout.add(publicatedCheckBox);

		reprocessSlideshowAndMiniCheckBox.setLabel("Přegenerovat slideshow a miniatury");
		contentOptionsLayout.add(reprocessSlideshowAndMiniCheckBox);

		photogalleryDateField.setLabel("Přepsat datum vytvoření galerie");
		contentOptionsLayout.add(photogalleryDateField);

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		buttonsLayout.setPadding(false);
		editorLayout.add(buttonsLayout);

		populateButtonsLayout(buttonsLayout);
	}

	private void populateButtonsLayout(HorizontalLayout buttonLayout) {

		// Uložit
		SaveButton saveButton = new SaveButton(event -> {
			if (!isFormValid())
				return;
			stayInEditor = true;
			saveOrUpdatePhotogallery();
		});
		buttonLayout.add(saveButton);

		// Uložit a zavřít
		SaveButton saveAndCloseButton = new SaveButton("Uložit a zavřít", event -> {
			if (!isFormValid())
				return;
			stayInEditor = false;
			saveOrUpdatePhotogallery();
		});
		buttonLayout.add(saveAndCloseButton);

		// Zrušit
		CloseButton cancelButton = new CloseButton("Zrušit", ev -> new ConfirmDialog(
				"Opravdu si přejete zavřít editor galerie ? Veškeré neuložené změny budou ztraceny.", e -> {
					cleanAfterCancelEdit();
					if (editMode)
						returnToPhotogallery();
					else
						returnToNode();
				}).open());
		buttonLayout.add(cancelButton);
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
		PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogalleryNameField.getValue(), galleryDir,
				photogalleryKeywords.getValues(), publicatedCheckBox.getValue(),
				reprocessSlideshowAndMiniCheckBox.getValue());

		eventBus.subscribe(PGEditorPage.this);
		progressIndicatorWindow = new ProgressDialog();

		if (editMode) {
			pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO,
					photogalleryDateField.getValue().atStartOfDay());
		} else {
			pgService.savePhotogallery(UUID.randomUUID(), payloadTO, node.getId(), getUser().getId(),
					photogalleryDateField.getValue().atStartOfDay());
		}
	}

	/**
	 * Zavolá vrácení se na galerii
	 */
	private void returnToPhotogallery() {
		Div closeJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void closeCallback() {
				UIUtils.redirect(getPageURL(photogalleryViewerPageFactory, URLIdentifierUtils
						.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
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

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			progressIndicatorWindow.open();
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

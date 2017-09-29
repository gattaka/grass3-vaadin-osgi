package cz.gattserver.grass3.pg.pages;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.events.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.PGProcessStartEvent;
import cz.gattserver.grass3.pg.facade.PhotogalleryFacade;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.template.MultiUpload;
import cz.gattserver.grass3.ui.progress.BaseProgressBar;
import cz.gattserver.grass3.ui.progress.ProgressWindow;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.URLPathAnalyzer;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.WarnWindow;

public class PhotogalleryEditorPage extends OneColumnPage {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory.getLogger(PhotogalleryEditorPage.class);

	@Autowired
	private NodeFacade nodeFacade;

	@Autowired
	private PhotogalleryFacade photogalleryFacade;

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "photogalleryViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Autowired
	private EventBus eventBus;

	private UI ui = UI.getCurrent();
	private ProgressWindow progressIndicatorWindow;

	private NodeDTO node;
	private PhotogalleryDTO photogallery;

	private AdvancedTokenField photogalleryKeywords;
	private TextField photogalleryNameField;
	private DateField photogalleryDateField;
	private CheckBox publicatedCheckBox;

	private File galleryDir;

	private boolean editMode;

	private List<Path> newFiles;

	private boolean stayInEditor = false;

	private boolean warnWindowDeployed = false;
	private Label existingFiles;
	private WarnWindow warnWindow;

	public PhotogalleryEditorPage(GrassRequest request) {
		super(request);
		JavaScript.eval(
				"window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
	}

	@Override
	protected void init() {

		newFiles = new ArrayList<>();

		photogalleryKeywords = new AdvancedTokenField();
		photogalleryNameField = new TextField();
		photogalleryDateField = new DateField();
		publicatedCheckBox = new CheckBox();

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String operationToken = analyzer.getNextPathToken();
		String identifierToken = analyzer.getNextPathToken();
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
			node = nodeFacade.getNodeByIdForOverview(identifier.getId());
			photogalleryNameField.setValue("");
			publicatedCheckBox.setValue(true);

		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {

			editMode = true;
			photogallery = photogalleryFacade.getPhotogalleryForDetail(identifier.getId());
			photogalleryNameField.setValue(photogallery.getContentNode().getName());

			for (ContentTagDTO tagDTO : photogallery.getContentNode().getContentTags()) {
				photogalleryKeywords.addToken(new Token(tagDTO.getName()));
			}

			publicatedCheckBox.setValue(photogallery.getContentNode().isPublicated());
			photogalleryDateField.setValue(photogallery.getContentNode().getCreationDate());

		} else {
			logger.debug("Neznámá operace: '" + operationToken + "'");
			showError404();
			return;
		}

		if ((photogallery == null || photogallery.getContentNode().getAuthor().equals(getGrassUI().getUser()))
				|| getGrassUI().getUser().getRoles().contains(Role.ADMIN)) {
			super.init();
		} else {
			// nemá oprávnění upravovat tento obsah
			showError403();
			return;
		}

	}

	@Override
	protected Component createContent() {

		final File dir = editMode ? photogalleryFacade.getGalleryDir(photogallery)
				: photogalleryFacade.createGalleryDir();
		galleryDir = dir;

		VerticalLayout editorLayout = new VerticalLayout();
		editorLayout.setSpacing(true);
		editorLayout.setMargin(true);

		VerticalLayout nameLayout = new VerticalLayout();
		editorLayout.addComponent(nameLayout);
		nameLayout.addComponent(new Label("<h2>Název galerie</h2>", ContentMode.HTML));
		nameLayout.addComponent(photogalleryNameField);
		photogalleryNameField.setWidth("100%");

		VerticalLayout keywordsLayout = new VerticalLayout();
		editorLayout.addComponent(keywordsLayout);

		// label
		keywordsLayout.addComponent(new Label("<h2>Klíčová slova</h2>", ContentMode.HTML));

		// menu tagů + textfield tagů
		// http://marc.virtuallypreinstalled.com/TokenField/
		HorizontalLayout keywordsMenuAndTextLayout = new HorizontalLayout();
		keywordsMenuAndTextLayout.setWidth("100%");
		keywordsMenuAndTextLayout.setSpacing(true);
		keywordsLayout.addComponent(keywordsMenuAndTextLayout);

		keywordsMenuAndTextLayout.addComponent(photogalleryKeywords);

		List<ContentTagDTO> contentTags = contentTagFacade.getContentTagsForOverview();
		// BeanContainer<String, ContentTagDTO> tokens = new
		// BeanContainer<String, ContentTagDTO>(ContentTagDTO.class);
		// tokens.setBeanIdProperty("name");
		// tokens.addAll(contentTags);

		// photogalleryKeywords.setStyleName(TokenField.STYLE_TOKENFIELD);
		// photogalleryKeywords.setContainerDataSource(tokens);
		contentTags.forEach(t -> {
			Token to = new Token(t.getName());
			photogalleryKeywords.addTokenToInputField(to);
		});
		// photogalleryKeywords.setFilteringMode(FilteringMode.CONTAINS); //
		// suggest
		// photogalleryKeywords.setTokenCaptionPropertyId("name");
		// photogalleryKeywords.setInputPrompt("klíčové slovo");
		// photogalleryKeywords.setRememberNewTokens(false);
		photogalleryKeywords.isEnabled();

		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setSpacing(true);
		editorLayout.addComponent(contentLayout);
		contentLayout.addComponent(new Label("<h2>Položky</h2>", ContentMode.HTML));

		final GridLayout gridLayout = new GridLayout(3, 2);
		gridLayout.setSpacing(true);
		gridLayout.setColumnExpandRatio(2, 1);
		gridLayout.setWidth("100%");
		contentLayout.addComponent(gridLayout);

		final Embedded image = new Embedded();
		image.setWidth("300px");
		image.setImmediate(true);

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

		final Table table = new Table();
		if (editMode) {

			BeanItemContainer<File> container = new BeanItemContainer<>(File.class,
					Arrays.asList(galleryDir.listFiles(new FileFilter() {

						@Override
						public boolean accept(File pathname) {
							return pathname.isDirectory() == false;
						}
					})));

			table.setContainerDataSource(container);
		} else {
			table.setContainerDataSource(new BeanItemContainer<>(File.class));
		}

		table.setSelectable(true);
		table.setSizeFull();
		table.setImmediate(true);
		table.setColumnHeader("name", "Název");
		table.setVisibleColumns(new Object[] { "name" });

		final Button removeBtn = new Button("Odstranit", new Button.ClickListener() {
			private static final long serialVersionUID = -4816423459867256516L;

			@Override
			public void buttonClick(ClickEvent event) {
				Object value = table.getValue();
				File file = (File) value;

				if (editMode) {
					if (PGUtils.isImage(file.getName())) {
						photogalleryFacade.tryDeleteMiniatureImage(file, photogallery);
						photogalleryFacade.tryDeleteSlideshowImage(file, photogallery);
					}

					if (PGUtils.isVideo(file.getName())) {
						photogalleryFacade.tryDeletePreviewImage(file, photogallery);
					}

				}

				file.delete();
				table.removeItem(file);
			}
		});
		removeBtn.setEnabled(false);
		gridLayout.addComponent(removeBtn, 1, 1);
		gridLayout.setComponentAlignment(removeBtn, Alignment.MIDDLE_CENTER);

		table.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -7300482828441860086L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				File file = (File) event.getProperty().getValue();

				if (file == null) {
					gridLayout.removeComponent(imageWrapper);
					gridLayout.addComponent(previewLabel, 0, 0, 1, 0);
					removeBtn.setEnabled(false);
				} else {
					if (PGUtils.isImage(file.getName())) {
						if (imageWrapper.getParent() == null) {
							gridLayout.removeComponent(previewLabel);
							gridLayout.addComponent(imageWrapper, 0, 0, 1, 0);
						}
						image.setSource(new FileResource(file));
					}
					removeBtn.setEnabled(true);
				}
			}
		});
		gridLayout.addComponent(table, 2, 0, 2, 1);

		VerticalLayout uploadWrapper = new VerticalLayout();
		uploadWrapper.setSpacing(true);
		uploadWrapper.setWidth("100%");
		uploadWrapper.setMargin(true);
		uploadWrapper.addStyleName("bordered");
		contentLayout.addComponent(uploadWrapper);

		final MultiUpload multiUpload = new MultiUpload() {
			private static final long serialVersionUID = -5223991901495532219L;

			@Override
			protected void handleFile(InputStream in, String fileName, String mimeType, long length) {
				Path path = Paths.get(dir.getPath(), fileName);
				try {
					Files.copy(in, path);
					newFiles.add(path);
					table.addItem(path.toFile());
				} catch (FileAlreadyExistsException f) {
					if (warnWindowDeployed == false) {
						existingFiles = new Label("", ContentMode.HTML);
						warnWindow = new WarnWindow("Následující soubory již existují:") {
							private static final long serialVersionUID = 3428203680996794639L;

							protected void createDetails() {
								addComponent(existingFiles);
							};

							public void close() {
								existingFiles.setValue("");
								warnWindowDeployed = false;
								super.close();
							};
						};
						UI.getCurrent().addWindow(warnWindow);
						warnWindowDeployed = true;
					}
					existingFiles.setValue(existingFiles.getValue() + fileName + "<br/>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		multiUpload.setCaption("Nahrát obsah");
		uploadWrapper.addComponent(multiUpload);
		uploadWrapper.setComponentAlignment(multiUpload, Alignment.MIDDLE_CENTER);

		VerticalLayout contentOptionsLayout = new VerticalLayout();
		contentOptionsLayout.setSpacing(true);
		editorLayout.addComponent(contentOptionsLayout);
		contentOptionsLayout.addComponent(new Label("<h2>Nastavení</h2>", ContentMode.HTML));

		publicatedCheckBox.setCaption("Publikovat galerii");
		publicatedCheckBox.setDescription("Je-li prázdné, uvidí galerii pouze její autor");
		publicatedCheckBox.setImmediate(true);
		contentOptionsLayout.addComponent(publicatedCheckBox);

		photogalleryDateField.setCaption("Přepsat datum vytvoření galerie");
		contentOptionsLayout.addComponent(photogalleryDateField);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(new MarginInfo(true, false, false, false));
		editorLayout.addComponent(buttonLayout);

		// Uložit
		Button saveButton = new Button("Uložit");
		saveButton.setIcon((com.vaadin.server.Resource) new ThemeResource(ImageIcons.SAVE_16_ICON));
		saveButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				if (isFormValid() == false)
					return;

				stayInEditor = true;
				saveOrUpdatePhotogallery();

			}

		});
		buttonLayout.addComponent(saveButton);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon((com.vaadin.server.Resource) new ThemeResource(ImageIcons.SAVE_16_ICON));
		saveAndCloseButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {
				if (isFormValid() == false)
					return;

				stayInEditor = false;
				saveOrUpdatePhotogallery();
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
						"Opravdu si přejete zavřít editor galerie ? Veškeré neuložené změny budou ztraceny.") {

					private static final long serialVersionUID = -3214040983143363831L;

					@Override
					protected void onConfirm(ClickEvent event) {
						// ruším úpravu existující galerie (vracím se na
						// galerii), nebo nové (vracím se do kategorie) ?
						if (editMode) {
							returnToPhotogallery();
						} else {
							returnToNode();
						}
					}
				};
				getUI().addWindow(confirmSubwindow);

			}

		});
		buttonLayout.addComponent(cancelButton);

		return editorLayout;
	}

	private boolean isFormValid() {

		String name = photogalleryNameField.getValue();

		if (name == null || name.isEmpty()) {
			showWarning("Název galerie nemůže být prázdný");
			return false;
		}

		return true;
	}

	private void saveOrUpdatePhotogallery() {

		System.out.println("saveOrUpdatePhotogallery thread: " + Thread.currentThread().getId());

		eventBus.subscribe(PhotogalleryEditorPage.this);
		ui.setPollInterval(200);
		List<String> tokens = new ArrayList<>();
		photogalleryKeywords.getTokens().forEach(t -> tokens.add(t.getValue()));
		if (editMode) {
			photogalleryFacade.modifyPhotogallery(String.valueOf(photogalleryNameField.getValue()), tokens,
					publicatedCheckBox.getValue(), photogallery, getRequest().getContextRoot(),
					photogalleryDateField.getValue());
		} else {
			photogalleryFacade.savePhotogallery(String.valueOf(photogalleryNameField.getValue()), tokens, galleryDir,
					publicatedCheckBox.getValue(), node, getGrassUI().getUser(), getRequest().getContextRoot(),
					photogalleryDateField.getValue());
		}
	}

	/**
	 * Zavolá vrácení se na galerii
	 */
	private void returnToPhotogallery() {
		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(photogalleryViewerPageFactory,
				URLIdentifierUtils.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
	}

	/**
	 * zavolání vrácení se na kategorii
	 */
	private void returnToNode() {
		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(nodePageFactory, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
	}

	@Override
	public void detach() {
		super.detach();
		for (Path file : newFiles) {
			try {
				Files.delete(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		ui.access(new Runnable() {
			@Override
			public void run() {
				BaseProgressBar progressBar = new BaseProgressBar(event.getCountOfStepsToDo());
				progressBar.setIndeterminate(false);
				progressBar.setValue(0f);
				progressIndicatorWindow = new ProgressWindow(progressBar);
				ui.addWindow(progressIndicatorWindow);
			}
		});
	}

	@Handler
	protected void onProcessProgress(PGProcessProgressEvent event) {
		ui.access(new Runnable() {
			@Override
			public void run() {
				progressIndicatorWindow.indicateProgress(event.getStepDescription());
			}
		});
	}

	@Handler
	protected void onProcessResult(final PGProcessResultEvent event) {
		ui.access(new Runnable() {
			@Override
			public void run() {

				// ui.setPollInterval(-1);
				if (progressIndicatorWindow != null)
					progressIndicatorWindow.closeOnDone();

				Long id = event.getGalleryId();
				if (event.isSuccess() && (id != null || editMode)) {
					if (editMode == false)
						photogallery = photogalleryFacade.getPhotogalleryForDetail(id);

					// soubory byly uloženy a nepodléhají
					// podmíněnému smazání
					newFiles.clear();
					if (stayInEditor == false)
						returnToPhotogallery();

					showInfo(editMode ? "Úprava galerie proběhla úspěšně" : "Uložení galerie proběhlo úspěšně");
				} else {
					showWarning(editMode ? "Úprava galerie se nezdařila" : "Uložení galerie se nezdařilo");
				}

				// odteď budeme editovat
				editMode = true;
			}
		});
		eventBus.unsubscribe(PhotogalleryEditorPage.this);
	}
}

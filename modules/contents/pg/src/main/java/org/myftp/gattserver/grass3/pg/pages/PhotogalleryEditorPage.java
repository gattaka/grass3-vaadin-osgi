package org.myftp.gattserver.grass3.pg.pages;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;
import org.myftp.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.subwindows.ConfirmWindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.template.MultiUpload;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.tokenfield.TokenField;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class PhotogalleryEditorPage extends OneColumnPage {

	private static final long serialVersionUID = -5148523174527532785L;

	private static final Logger logger = LoggerFactory.getLogger(PhotogalleryEditorPage.class);

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "photogalleryFacade")
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "photogalleryViewerPageFactory")
	private IPageFactory photogalleryViewerPageFactory;

	private NodeDTO category;
	private PhotogalleryDTO photogallery;

	private final TokenField photogalleryKeywords = new TokenField();
	private final TextField photogalleryNameField = new TextField();
	private final CheckBox publicatedCheckBox = new CheckBox();

	private File galleryDir;

	private boolean editMode = false;

	public PhotogalleryEditorPage(GrassRequest request) {
		super(request);
		JavaScript
				.eval("window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - rozpracovaná data nejsou uložena ?\" };");
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

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '" + identifierToken + "'");
			showError404();
		}

		// operace ?
		if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
			editMode = false;
			category = nodeFacade.getNodeByIdForOverview(identifier.getId());
			photogalleryNameField.setValue("");
			publicatedCheckBox.setValue(true);

		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {

			editMode = true;
			photogallery = photogalleryFacade.getPhotogalleryForDetail(identifier.getId());
			photogalleryNameField.setValue(photogallery.getContentNode().getName());

			for (ContentTagDTO tagDTO : photogallery.getContentNode().getContentTags()) {
				photogalleryKeywords.addToken(tagDTO.getName());
			}

			publicatedCheckBox.setValue(photogallery.getContentNode().isPublicated());

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

		final File dir = editMode ? photogalleryFacade.getGalleryDir(photogallery) : photogalleryFacade
				.createGalleryDir();
		galleryDir = dir;

		VerticalLayout editorTextLayout = new VerticalLayout();
		editorTextLayout.setSpacing(true);
		editorTextLayout.setMargin(true);

		VerticalLayout nameLayout = new VerticalLayout();
		editorTextLayout.addComponent(nameLayout);
		nameLayout.addComponent(new Label("<h2>Název galerie</h2>", ContentMode.HTML));
		nameLayout.addComponent(photogalleryNameField);
		photogalleryNameField.setWidth("100%");

		VerticalLayout keywordsLayout = new VerticalLayout();
		editorTextLayout.addComponent(keywordsLayout);

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
		BeanContainer<String, ContentTagDTO> tokens = new BeanContainer<String, ContentTagDTO>(ContentTagDTO.class);
		tokens.setBeanIdProperty("name");
		tokens.addAll(contentTags);

		photogalleryKeywords.setStyleName(TokenField.STYLE_TOKENFIELD);
		photogalleryKeywords.setContainerDataSource(tokens);
		photogalleryKeywords.setFilteringMode(FilteringMode.CONTAINS); // suggest
		photogalleryKeywords.setTokenCaptionPropertyId("name");
		photogalleryKeywords.setInputPrompt("klíčové slovo");
		photogalleryKeywords.setRememberNewTokens(false);
		photogalleryKeywords.isEnabled();

		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setSpacing(true);
		editorTextLayout.addComponent(contentLayout);
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
		imageWrapper.addComponent(image);
		imageWrapper.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
		// gridLayout.addComponent(imageWrapper, 0, 0, 1, 0);

		final Table table = new Table();
		if (editMode) {

			BeanItemContainer<File> container = new BeanItemContainer<>(File.class, Arrays.asList(galleryDir
					.listFiles(new FileFilter() {

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

		final Button renameBtn = new Button("Přejmenovat", new Button.ClickListener() {
			private static final long serialVersionUID = -4816423459867256516L;

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
			}
		});
		renameBtn.setEnabled(false);
		gridLayout.addComponent(renameBtn, 0, 1);
		gridLayout.setComponentAlignment(renameBtn, Alignment.MIDDLE_CENTER);

		final Button removeBtn = new Button("Odstranit", new Button.ClickListener() {
			private static final long serialVersionUID = -4816423459867256516L;

			@Override
			public void buttonClick(ClickEvent event) {
				Object value = table.getValue();
				File file = (File) value;

				if (editMode)
					photogalleryFacade.tryDeleteMiniature(file, photogallery);

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
					renameBtn.setEnabled(false);
					removeBtn.setEnabled(false);
				} else {
					if (imageWrapper.getParent() == null) {
						gridLayout.removeComponent(previewLabel);
						gridLayout.addComponent(imageWrapper, 0, 0, 1, 0);
					}
					renameBtn.setEnabled(true);
					removeBtn.setEnabled(true);
					image.setSource(new FileResource(file));
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
			protected void handleFile(File file, String fileName, String mimeType, long length) {

				File movedFile = new File(dir, fileName);
				if (file.renameTo(movedFile)) {
					table.addItem(movedFile);
				}
			}
		};
		multiUpload.setCaption("Nahrát fotografie");
		uploadWrapper.addComponent(multiUpload);
		uploadWrapper.setComponentAlignment(multiUpload, Alignment.MIDDLE_CENTER);

		VerticalLayout contentOptionsLayout = new VerticalLayout();
		editorTextLayout.addComponent(contentOptionsLayout);
		contentOptionsLayout.addComponent(new Label("<h2>Nastavení</h2>", ContentMode.HTML));

		publicatedCheckBox.setCaption("Publikovat galerii");
		publicatedCheckBox.setDescription("Je-li prázdné, uvidí galerii pouze její autor");
		publicatedCheckBox.setImmediate(true);
		contentOptionsLayout.addComponent(publicatedCheckBox);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(new MarginInfo(true, false, false, false));
		editorTextLayout.addComponent(buttonLayout);

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

				if (saveOrUpdatePhotogallery()) {
					showInfo(oldMode ? "Úprava galerie proběhla úspěšně" : "Uložení galerie proběhlo úspěšně");
				} else {
					showWarning(oldMode ? "Úprava galerie se nezdařila" : "Uložení galerie se nezdařilo");
				}

			}

		});
		buttonLayout.addComponent(saveButton);

		// Uložit a zavřít
		Button saveAndCloseButton = new Button("Uložit a zavřít");
		saveAndCloseButton.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/save_16.png"));
		saveAndCloseButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				if (isFormValid() == false)
					return;

				if (saveOrUpdatePhotogallery()) {
					returnToPhotogallery();
				} else {
					showWarning(editMode ? "Úprava galerie se nezdařila" : "Uložení galerie se nezdařilo");
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
						"Opravdu si přejete zavřít editor galerie ? Veškeré neuložené změny budou ztraceny.") {

					private static final long serialVersionUID = -3214040983143363831L;

					@Override
					protected void onConfirm(ClickEvent event) {
						// ruším úpravu existující galerie (vracím se na
						// galerii), nebo nové (vracím se do kategorie) ?
						if (editMode) {
							returnToPhotogallery();
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

		String name = photogalleryNameField.getValue();

		if (name == null || name.isEmpty()) {
			showWarning("Název galerie nemůže být prázdný");
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean saveOrUpdatePhotogallery() {

		if (editMode) {

			return photogalleryFacade.modifyPhotogallery(String.valueOf(photogalleryNameField.getValue()),
					(Collection<String>) photogalleryKeywords.getValue(), publicatedCheckBox.getValue(), photogallery,
					getRequest().getContextRoot());
		} else {
			Long id = photogalleryFacade.savePhotogallery(String.valueOf(photogalleryNameField.getValue()),
					(Collection<String>) photogalleryKeywords.getValue(), galleryDir, publicatedCheckBox.getValue(),
					category, getGrassUI().getUser(), getRequest().getContextRoot());

			if (id == null)
				return false;

			// odteď budeme editovat
			editMode = true;
			photogallery = photogalleryFacade.getPhotogalleryForDetail(id);
			return true;
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
	private void returnToCategory() {
		JavaScript.eval("window.onbeforeunload = null;");
		redirect(getPageURL(categoryPageFactory,
				URLIdentifierUtils.createURLIdentifier(category.getId(), category.getName())));
	}

}

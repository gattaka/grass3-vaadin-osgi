package cz.gattserver.grass3.campgames.ui.windows;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.campgames.CampgamesConfiguration;
import cz.gattserver.grass3.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ImageButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.HtmlSpan;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public class CampgameDetailDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	private Tabs sheet;
	private CampgameTO campgameTO;
	private Long campgameId;

	private ChangeListener changeListener;

	private Map<Tab, Div> tabToDiv = new HashMap<>();
	private Tab imgTab;

	public CampgameDetailDialog(Long campgameId) {
		super("Detail hry");
		this.campgameId = campgameId;

		setWidth("680px");
		setHeight("600px");

		sheet = new Tabs();
		sheet.setSizeFull();
		createFirstTab();
		imgTab = new Tab();
		imgTab.setLabel(createPhotosTabCaption());
		imgTab.add(new Image(ImageIcon.IMG_16_ICON.createResource(), "icon"));
		tabToDiv.put(imgTab, createPhotosTab());
		sheet.add(imgTab);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setSizeFull();

		H3 name = new H3(campgameTO.getName());
		layout.add(name);
		layout.add(sheet);
		add(layout);
	}

	private String createPhotosTabCaption() {
		return "Fotografie (" + getCampgamesService().getCampgameImagesFilesCount(campgameId) + ")";
	}

	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	private VerticalLayout createWrapperLayout() {
		VerticalLayout wrapperLayout = new VerticalLayout();
		wrapperLayout.setSpacing(true);
		wrapperLayout.setSizeFull();
		return wrapperLayout;
	}

	private VerticalLayout createItemDetailsLayout(CampgameTO campgameTO) {
		VerticalLayout wrapperLayout = createWrapperLayout();

		VerticalLayout itemLayout = new VerticalLayout();
		itemLayout.setSpacing(true);
		itemLayout.setPadding(false);
		itemLayout.setSizeFull();
		wrapperLayout.add(itemLayout);

		/**
		 * Keywords
		 */
		Div tags = new Div();
		campgameTO.getKeywords().forEach(keyword -> {
			Span token = new Span(keyword);
			token.setSizeUndefined();
			token.addClassName("read-only-token");
			tags.add(token);
		});
		if (!campgameTO.getKeywords().isEmpty())
			itemLayout.add(tags);

		FormLayout metaInfoLayout = new FormLayout();
		itemLayout.add(metaInfoLayout);

		metaInfoLayout.add(new HtmlSpan("<strong>Původ:</strong> " + campgameTO.getOrigin()));
		metaInfoLayout.add(new HtmlSpan("<strong>Počet hráčů:</strong> " + campgameTO.getPlayers()));
		metaInfoLayout.add(new HtmlSpan("<strong>Délka hry:</strong> " + campgameTO.getPlayTime()));
		metaInfoLayout.add(new HtmlSpan("<strong>Délka přípravy:</strong> " + campgameTO.getPreparationTime()));

		HtmlDiv descLabel = new HtmlDiv(campgameTO.getDescription().replaceAll("\n", "<br/>"));
		descLabel.setSizeFull();
		VerticalLayout layout = new VerticalLayout(descLabel);
		layout.setPadding(true);

		Div panel = new Div(layout);
		panel.setWidth("100%");
		panel.setHeight("100%");
		itemLayout.add(panel);

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		wrapperLayout.add(operationsLayout);

		operationsLayout.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CoreRole.ADMIN));

		/**
		 * Oprava údajů existující hry
		 */
		final ModifyButton fixBtn = new ModifyButton(e -> new CampgameCreateWindow(campgameTO) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(CampgameTO dto) {
				if (changeListener != null)
					changeListener.onChange();
				createFirstTab();
				sheet.setSelectedIndex(0);
			}
		}.open());
		operationsLayout.add(fixBtn);

		/**
		 * Smazání hry
		 */
		final DeleteButton deleteBtn = new DeleteButton(
				e -> new ConfirmDialog("Opravdu smazat '" + campgameTO.getName() + "' ?", ev -> {
					try {
						getCampgamesService().deleteCampgame(campgameId);
						if (changeListener != null)
							changeListener.onChange();
						CampgameDetailDialog.this.close();
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
					}
				}).open());
		operationsLayout.add(deleteBtn);

		return wrapperLayout;
	}

	private Div createPhotosTab() {
		final Div panel = new Div();
		panel.setSizeFull();

		createImagesList(panel);

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
		upload.addSucceededListener(event -> {
			getCampgamesService().saveImagesFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
					campgameTO);
			// refresh listu
			panel.removeAll();
			createImagesList(panel);
			imgTab.setLabel(createPhotosTabCaption());
		});
		panel.add(upload);
		upload.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CoreRole.ADMIN));

		return panel;
	}

	private void createImagesList(Div listLayout) {
		for (final CampgameFileTO file : getCampgamesService().getCampgameImagesFiles(campgameId)) {

			VerticalLayout imageLayout = new VerticalLayout();
			listLayout.add(imageLayout);
			imageLayout.setSpacing(true);

			Image img = new Image(
					new StreamResource(file.getName(),
							() -> getCampgamesService().getCampgameImagesFileInputStream(campgameId, file.getName())),
					file.getName());
			img.addClassName("thumbnail-200");
			imageLayout.add(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			ImageButton imageDetailBtn = new ImageButton("Detail", ImageIcon.SEARCH_16_ICON,
					e -> UI.getCurrent().getPage().open(
							CampgamesConfiguration.CAMPGAMES_PATH + "/" + campgameTO.getId() + "/" + file.getName()));

			DeleteButton imageDeleteBtn = new DeleteButton(
					e -> UI.getCurrent().add(new ConfirmDialog("Opravdu smazat foto?", ev -> {
						getCampgamesService().deleteCampgameImagesFile(campgameId, file.getName());

						// refresh listu
						listLayout.removeAll();
						createImagesList(listLayout);
						imgTab.setLabel(createPhotosTabCaption());
					})));

			btnLayout.add(imageDetailBtn);
			btnLayout.add(imageDeleteBtn);

			imageDeleteBtn.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
					.contains(CoreRole.ADMIN));

			imageLayout.add(btnLayout);
		}
	}

	private void createFirstTab() {
		this.campgameTO = getCampgamesService().getCampgame(campgameId);
		// Tab tab = sheet.getTab(0);
		// if (tab != null)
		// sheet.removeTab(tab);
		// Tab detailsTab = new Tab();
		// sheet.addTab(createItemDetailsLayout(campgameTO), "Info",
		// ImageIcon.GEAR2_16_ICON.createResource(), 0);
	}

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public CampgameDetailDialog setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
		return this;
	}

}

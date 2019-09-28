package cz.gattserver.grass3.campgames.ui.dialogs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
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
import cz.gattserver.grass3.ui.components.button.CloseButton;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.ImageButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public class CampgameDetailDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	private CampgameTO campgameTO;
	private Long campgameId;

	private ChangeListener changeListener;

	private Tabs tabSheet;
	private Tab detailsTab;
	private Tab imgTab;

	private VerticalLayout pageLayout;

	public CampgameDetailDialog(Long campgameId) {
		this.campgameId = campgameId;
		this.campgameTO = getCampgamesService().getCampgame(campgameId);

		layout.setWidth("720px");
		layout.setHeight("600px");
		layout.setSpacing(false);

		tabSheet = new Tabs();
		tabSheet.setWidthFull();
		layout.add(tabSheet);

		pageLayout = new VerticalLayout();
		pageLayout.setPadding(false);
		pageLayout.setSpacing(false);
		pageLayout.setSizeFull();
		layout.add(pageLayout);

		detailsTab = new Tab("Info");
		tabSheet.add(detailsTab);

		imgTab = new Tab(createImgTabCaption());
		tabSheet.add(imgTab);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchDetailsTab();
				break;
			case 1:
				switchImgTab();
				break;
			}
		});
		switchDetailsTab();

		setCloseOnEsc(true);
		setCloseOnOutsideClick(true);
	}

	private void switchDetailsTab() {
		pageLayout.removeAll();
		pageLayout.add(createItemDetailsLayout(campgameTO));
		tabSheet.setSelectedTab(detailsTab);
	}

	private void switchImgTab() {
		pageLayout.removeAll();
		pageLayout.add(createImgTab());
		tabSheet.setSelectedTab(imgTab);
	}

	private String createImgTabCaption() {
		return "Fotografie (" + getCampgamesService().getCampgameImagesFilesCount(campgameId) + ")";
	}

	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	private Component createItemDetailsLayout(CampgameTO campgameTO) {

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setPadding(false);
		layout.setSpacing(false);

		/**
		 * Keywords
		 */
		ButtonLayout tags = new ButtonLayout();
		campgameTO.getKeywords().forEach(keyword -> {
			Button token = new Button(keyword);
			tags.add(token);
		});
		if (!campgameTO.getKeywords().isEmpty())
			layout.add(tags);

		HtmlDiv table = new HtmlDiv("<table>" + "<tr><td><strong>Název:</strong></td><td>" + campgameTO.getName()
				+ "</td></tr>" + "<tr><td><strong>Původ:</strong></td><td>" + campgameTO.getOrigin()
				+ "</td><td><strong>Počet hráčů:</strong></td><td>" + campgameTO.getPlayers() + "</td></tr>"
				+ "<tr><td><strong>Délka hry:</strong></td><td>" + campgameTO.getPlayTime()
				+ "</td><td><strong>Délky přípravy:</strong></td><td>" + campgameTO.getPreparationTime() + "</td></tr>"
				+ "</table>");
		table.addClassName("top-margin");
		layout.add(table);

		HtmlDiv descDiv = new HtmlDiv(campgameTO.getDescription().replaceAll("\n", "<br/>"));
		descDiv.addClassName("top-margin");
		descDiv.addClassName("scroll-div");
		descDiv.setSizeFull();
		layout.add(descDiv);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.addClassName("top-margin");
		btnLayout.setWidthFull();
		btnLayout.setSpacing(false);
		btnLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		layout.add(btnLayout);

		Div operationsLayout = new Div();
		operationsLayout.addClassName("button-div");
		btnLayout.add(operationsLayout);
		operationsLayout.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CoreRole.ADMIN));

		/**
		 * Oprava údajů existující hry
		 */
		final ModifyButton fixBtn = new ModifyButton(e -> new CampgameCreateDialog(campgameTO) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(CampgameTO dto) {
				if (changeListener != null)
					changeListener.onChange();
				switchDetailsTab();
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

		CloseButton closeBtn = new CloseButton(e -> close());
		btnLayout.add(closeBtn);

		return layout;
	}

	private Div createImgTab() {
		Div tabLayout = new Div();
		tabLayout.setSizeFull();

		Div panelWrapper = new Div();
		panelWrapper.addClassName("scroll-div");
		panelWrapper.addClassName("top-margin");
		panelWrapper.getStyle().set("height", "calc(100% - 85px)");
		tabLayout.add(panelWrapper);

		final FormLayout panel = new FormLayout();
		panel.addClassName("top-margin");
		panel.setResponsiveSteps(new FormLayout.ResponsiveStep("200px", 3));

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
		upload.addSucceededListener(event -> {
			getCampgamesService().saveImagesFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
					campgameTO);
			// refresh listu
			panel.removeAll();
			createImagesList(panel);
			imgTab.setLabel(createImgTabCaption());
		});
		tabLayout.add(upload);
		upload.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CoreRole.ADMIN));

		panelWrapper.add(panel);

		createImagesList(panel);

		return tabLayout;
	}

	private void createImagesList(FormLayout listLayout) {
		for (final CampgameFileTO file : getCampgamesService().getCampgameImagesFiles(campgameId)) {

			VerticalLayout imageLayout = new VerticalLayout();
			listLayout.add(imageLayout);
			imageLayout.setWidth("200px");
			imageLayout.setPadding(false);

			Div imgWrapper = new Div();
			imgWrapper.setWidth("200px");
			imgWrapper.setHeight("200px");
			Image img = new Image(
					new StreamResource(file.getName(),
							() -> getCampgamesService().getCampgameImagesFileInputStream(campgameId, file.getName())),
					file.getName());
			img.addClassName("thumbnail-200");
			imgWrapper.add(img);
			imageLayout.add(imgWrapper);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
			btnLayout.setSpacing(false);
			btnLayout.setWidth("200px");

			ImageButton imageDetailBtn = new ImageButton("Detail", ImageIcon.SEARCH_16_ICON,
					e -> UI.getCurrent().getPage().open(
							CampgamesConfiguration.CAMPGAMES_PATH + "/" + campgameTO.getId() + "/" + file.getName()));

			DeleteButton imageDeleteBtn = new DeleteButton(e -> new ConfirmDialog("Opravdu smazat foto?", ev -> {
				getCampgamesService().deleteCampgameImagesFile(campgameId, file.getName());

				// refresh listu
				listLayout.removeAll();
				createImagesList(listLayout);
				imgTab.setLabel(createImgTabCaption());
			}).open());

			btnLayout.add(imageDetailBtn);
			btnLayout.add(imageDeleteBtn);

			imageDeleteBtn.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
					.contains(CoreRole.ADMIN));

			imageLayout.add(btnLayout);
		}
	}

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public CampgameDetailDialog setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
		return this;
	}

}

package cz.gattserver.grass3.campgames.ui.windows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.exception.SystemException;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.ImageDetailWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public class CampgameDetailWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	private TabSheet sheet;
	private CampgameTO campgameTO;
	private Long campgameId;

	private ChangeListener changeListener;

	public CampgameDetailWindow(Long campgameId, GrassRequest grassRequest) {
		super("Detail hry");
		this.campgameId = campgameId;

		setWidth("900px");
		setHeight("700px");

		sheet = new TabSheet();
		sheet.setSizeFull();
		createFirstTab();
		sheet.addTab(createPhotosTab(), createPhotosTabCaption(), ImageIcon.IMG_16_ICON.createResource());

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(new MarginInfo(false, true, true, true));
		layout.setSizeFull();

		Label name = new Label("<h3>" + campgameTO.getName() + "</h3>", ContentMode.HTML);
		layout.addComponent(name);
		layout.addComponent(sheet);
		layout.setExpandRatio(sheet, 1);
		setContent(layout);

		center();
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
		wrapperLayout.setMargin(new MarginInfo(true, false, false, false));
		wrapperLayout.setSizeFull();
		return wrapperLayout;
	}

	private Layout createItemDetailsLayout(CampgameTO campgameTO) {

		VerticalLayout wrapperLayout = createWrapperLayout();

		VerticalLayout itemLayout = new VerticalLayout();
		itemLayout.setSpacing(true);
		itemLayout.setMargin(false);
		itemLayout.setSizeUndefined();
		wrapperLayout.addComponent(itemLayout);

		/**
		 * Keywords
		 */
		HorizontalLayout tags = new HorizontalLayout();
		tags.setSpacing(true);
		campgameTO.getKeywords().forEach(keyword -> {
			Label token = new Label(keyword);
			token.setSizeUndefined();
			token.setStyleName("read-only-token");
			tags.addComponent(token);
		});
		itemLayout.addComponent(tags);

		itemLayout.addComponent(new Label("<strong>Původ:</strong> " + campgameTO.getOrigin(), ContentMode.HTML));
		itemLayout
				.addComponent(new Label("<strong>Počet hráčů:</strong> " + campgameTO.getPlayers(), ContentMode.HTML));
		itemLayout.addComponent(new Label("<strong>Délka hry:</strong> " + campgameTO.getPlayTime(), ContentMode.HTML));
		itemLayout.addComponent(
				new Label("<strong>Délka přípravy:</strong> " + campgameTO.getPreparationTime(), ContentMode.HTML));
		itemLayout.addComponent(new Label(campgameTO.getDescription(), ContentMode.HTML));

		VerticalLayout partsWrapperLayout = new VerticalLayout();
		partsWrapperLayout.setSpacing(false);
		partsWrapperLayout.setMargin(false);
		partsWrapperLayout.setSizeFull();
		wrapperLayout.addComponent(partsWrapperLayout);
		wrapperLayout.setComponentAlignment(partsWrapperLayout, Alignment.TOP_LEFT);
		wrapperLayout.setExpandRatio(partsWrapperLayout, 1);

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		wrapperLayout.addComponent(operationsLayout);

		/**
		 * Oprava údajů existující hry
		 */
		final Button fixBtn = new ModifyButton(e -> UI.getCurrent().addWindow(new CampgameCreateWindow(campgameTO) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(CampgameTO dto) {
				if (changeListener != null)
					changeListener.onChange();
				createFirstTab();
				sheet.setSelectedTab(0);
			}
		}));
		operationsLayout.addComponent(fixBtn);

		/**
		 * Smazání hry
		 */
		final Button deleteBtn = new DeleteButton(e -> UI.getCurrent()
				.addWindow(new ConfirmWindow("Opravdu smazat '" + campgameTO.getName() + "' ?", ev -> {
					try {
						getCampgamesService().deleteCampgame(campgameId);
						if (changeListener != null)
							changeListener.onChange();
						CampgameDetailWindow.this.close();
					} catch (Exception ex) {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				})));
		operationsLayout.addComponent(deleteBtn);

		return wrapperLayout;
	}

	private Layout createPhotosTab() {
		VerticalLayout wrapperLayout = createWrapperLayout();

		GridLayout listLayout = new GridLayout();
		listLayout.setColumns(4);
		listLayout.setSpacing(true);
		listLayout.setMargin(true);

		Panel panel = new Panel(listLayout);
		panel.setSizeFull();
		wrapperLayout.addComponent(panel);
		wrapperLayout.setExpandRatio(panel, 1);

		createImagesList(listLayout);

		MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = -3899558855555370125L;

			@Override
			protected void fileUploadFinished(InputStream in, String fileName, String mimeType, long length,
					int filesLeftInQueue) {
				getCampgamesService().saveImagesFile(in, fileName, campgameTO);
				// refresh listu
				listLayout.removeAllComponents();
				createImagesList(listLayout);
				sheet.getTab(1).setCaption(createPhotosTabCaption());
			}
		};

		multiFileUpload.setCaption("Vložit fotografie");
		multiFileUpload.setSizeUndefined();
		wrapperLayout.addComponent(multiFileUpload);

		return wrapperLayout;
	}

	private void createImagesList(GridLayout listLayout) {
		for (final CampgameFileTO file : getCampgamesService().getCampgameImagesFiles(campgameId)) {

			VerticalLayout imageLayout = new VerticalLayout();
			listLayout.addComponent(imageLayout);
			imageLayout.setSpacing(true);
			imageLayout.setMargin(false);

			Resource resource = new StreamResource(
					() -> getCampgamesService().getCampgameImagesFileInputStream(campgameId, file.getName()),
					file.getName());
			Embedded img = new Embedded(null, resource);
			img.addStyleName("thumbnail-200");
			imageLayout.addComponent(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			Button imageDetailBtn = new Button("Detail", e -> {
				BufferedImage bimg = null;
				try {
					bimg = ImageIO
							.read(getCampgamesService().getCampgameImagesFileInputStream(campgameId, file.getName()));
					int width = bimg.getWidth();
					int height = bimg.getHeight();
					UI.getCurrent()
							.addWindow(new ImageDetailWindow(campgameTO.getName(), width, height,
									new StreamResource(() -> getCampgamesService()
											.getCampgameImagesFileInputStream(campgameId, file.getName()),
											file.getName())));
				} catch (IOException ex) {
					throw new SystemException("Při čtení souboru '" + file.getName() + "' došlo k chybě.", ex);
				}
			});

			Button imageDeleteBtn = new DeleteButton(
					e -> UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto?", ev -> {
						getCampgamesService().deleteCampgameImagesFile(campgameId, file.getName());

						// refresh listu
						listLayout.removeAllComponents();
						createImagesList(listLayout);
						sheet.getTab(1).setCaption(createPhotosTabCaption());
					})));

			imageDetailBtn.setIcon(ImageIcon.SEARCH_16_ICON.createResource());
			imageDeleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

			btnLayout.addComponent(imageDetailBtn);
			btnLayout.addComponent(imageDeleteBtn);

			imageLayout.addComponent(btnLayout);
			imageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		}
	}

	private void createFirstTab() {
		this.campgameTO = getCampgamesService().getCampgame(campgameId);
		Tab tab = sheet.getTab(0);
		if (tab != null)
			sheet.removeTab(tab);
		sheet.addTab(createItemDetailsLayout(campgameTO), "Info", ImageIcon.GEAR2_16_ICON.createResource(), 0);
	}

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public CampgameDetailWindow setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
		return this;
	}

}

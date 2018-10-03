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

	private static final String STATE_BIND = "customState";
	private static final String DATE_BIND = "customDate";

	private transient CampgamesService hwService;

	private TabSheet sheet;
	private CampgameTO campgameTO;
	private Long campgameId;

	private GrassRequest grassRequest;

	private ChangeListener changeListener;

	public CampgameDetailWindow(Long hwItemId, GrassRequest grassRequest) {
		super("Detail HW");
		this.campgameId = hwItemId;
		this.grassRequest = grassRequest;

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
		if (hwService == null)
			hwService = SpringContextHelper.getBean(CampgamesService.class);
		return hwService;
	}

	private VerticalLayout createWrapperLayout() {
		VerticalLayout wrapperLayout = new VerticalLayout();
		wrapperLayout.setSpacing(true);
		wrapperLayout.setMargin(new MarginInfo(true, false, false, false));
		wrapperLayout.setSizeFull();
		return wrapperLayout;
	}

	private Layout createItemDetailsLayout(CampgameTO hwItem) {

		VerticalLayout wrapperLayout = createWrapperLayout();

		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(false);
		itemLayout.setMargin(false);
		itemLayout.setSizeUndefined();
		wrapperLayout.addComponent(itemLayout);

		/**
		 * Grid
		 */
		GridLayout winLayout = new GridLayout(5, 7);
		itemLayout.addComponent(winLayout);
		itemLayout.setComponentAlignment(winLayout, Alignment.TOP_LEFT);
		winLayout.setSpacing(true);
		winLayout.setMargin(false);

		/**
		 * Keywords
		 */
		HorizontalLayout tags = new HorizontalLayout();
		tags.setSpacing(true);
		hwItem.getKeywords().forEach(keyword -> {
			Label token = new Label(keyword);
			token.setSizeUndefined();
			token.setStyleName("read-only-token");
			tags.addComponent(token);
		});
		winLayout.addComponent(tags, 1, 0, 3, 0);

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
		 * Oprava údajů existující položky HW
		 */
		final Button fixBtn = new ModifyButton(e -> UI.getCurrent().addWindow(new CampgameCreateWindow(hwItem) {
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
		 * Smazání položky HW
		 */
		final Button deleteBtn = new DeleteButton(e -> UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu smazat '" + hwItem.getName() + "' ?",
				ev -> {
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
				sheet.getTab(2).setCaption(createPhotosTabCaption());
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

			Button hwItemImageDetailBtn = new Button("Detail", e -> {
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

			Button hwItemImageDeleteBtn = new DeleteButton(
					e -> UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto HW položky ?", ev -> {
						getCampgamesService().deleteCampgameImagesFile(campgameId, file.getName());

						// refresh listu
						listLayout.removeAllComponents();
						createImagesList(listLayout);
						sheet.getTab(2).setCaption(createPhotosTabCaption());
					})));

			hwItemImageDetailBtn.setIcon(ImageIcon.SEARCH_16_ICON.createResource());
			hwItemImageDeleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

			btnLayout.addComponent(hwItemImageDetailBtn);
			btnLayout.addComponent(hwItemImageDeleteBtn);

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

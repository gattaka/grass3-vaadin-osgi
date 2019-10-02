package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessStartEvent;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryItemType;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.components.button.ImageButton;
import cz.gattserver.grass3.ui.dialogs.ImageSlideshowWindow;
import cz.gattserver.grass3.ui.dialogs.ProgressDialog;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.ContentViewerPage;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.WarnDialog;
import net.engio.mbassy.listener.Handler;

@Route("photogallery")
public class PGViewerPage extends ContentViewerPage implements HasUrlParameter<String>, HasDynamicTitle {

	private static final long serialVersionUID = 7334408385869747381L;

	private static final Logger logger = LoggerFactory.getLogger(PGViewerPage.class);
	private static final String ROWS_STATUS_PREFIX = "Řádky: ";
	private static final String IMAGE_SUM_PREFIX = " | Položek: ";

	@Autowired
	private PGService pgService;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Resource(name = "pgEditorPageFactory")
	private PageFactory photogalleryEditorPageFactory;

	@Autowired
	private EventBus eventBus;

	private ProgressDialog progressIndicatorWindow;

	private PhotogalleryTO photogallery;

	private int rowsSum;
	private int imageSum;
	private int galleryGridRowOffset;
	private static final int GALLERY_GRID_COLS = 4;
	private static final int GALLERY_GRID_ROWS = 3;

	private Span rowStatusLabel;

	private Button upRowBtn;
	private Button downRowBtn;
	private Button upPageBtn;
	private Button downPageBtn;
	private Button startPageBtn;
	private Button endPageBtn;

	private PGMultiUpload upload;

	private FormLayout galleryGridLayout;

	/**
	 * Položka z fotogalerie, která byla dle URL vybrána (nepovinné)
	 */
	private Integer pgSelected;
	private String galleryDir;

	private String identifierToken;
	private String magickToken;

	@Override
	public String getPageTitle() {
		return photogallery.getContentNode().getName();
	}

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		if (chunks.length > 0)
			identifierToken = chunks[0];
		if (chunks.length > 1)
			magickToken = chunks[1];

		init();
	}

	private boolean isAdminOrAuthor() {
		return getUser().isAdmin() || photogallery.getContentNode().getAuthor().equals(getUser());
	}

	@Override
	protected ContentNodeTO getContentNodeDTO() {
		return photogallery.getContentNode();
	}

	private void configureBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.setSpacing(true);
		btnLayout.setWidth("100%");
	}

	@Override
	protected void createContent(Div layout) {
		rowsSum = 0;
		imageSum = 0;
		galleryGridRowOffset = 0;

		rowStatusLabel = new Span();
		rowStatusLabel.setSizeUndefined();

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null)
			throw new GrassPageException(404);

		photogallery = pgService.getPhotogalleryForDetail(identifier.getId());
		if (photogallery == null)
			throw new GrassPageException(404);

		if (!"MAG1CK".equals(magickToken) && !photogallery.getContentNode().isPublicated() && !isAdminOrAuthor())
			throw new GrassPageException(403);

		galleryDir = photogallery.getPhotogalleryPath();

		// pokud je galerie porušená, pak nic nevypisuj
		try {
			if (!pgService.checkGallery(galleryDir)) {
				layout.add(new Span("Chyba: Galerie je porušená -- kontaktujte administrátora (ID: "
						+ photogallery.getPhotogalleryPath() + ")"));
				return;
			}
		} catch (IllegalStateException e) {
			throw new GrassPageException(500, e);
		} catch (IllegalArgumentException e) {
			throw new GrassPageException(404, e);
		}

		try {
			imageSum = pgService.getViewItemsCount(photogallery.getPhotogalleryPath());
		} catch (Exception e) {
			throw new GrassPageException(500, e);
		}
		rowsSum = (int) Math.ceil((double) imageSum / GALLERY_GRID_COLS);

		VerticalLayout galleryLayout = new VerticalLayout();
		galleryLayout.setSpacing(true);
		galleryLayout.addClassName("bordered");

		galleryGridLayout = new FormLayout();
		galleryGridLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("200px", GALLERY_GRID_COLS));
		galleryGridLayout.setSizeFull();

		// Horní layout tlačítek
		HorizontalLayout topBtnsLayout = new HorizontalLayout();
		configureBtnLayout(topBtnsLayout);
		layout.add(topBtnsLayout);

		layout.add(galleryLayout);

		// Spodní layout tlačítek
		HorizontalLayout bottomBtnsLayout = new HorizontalLayout();
		configureBtnLayout(bottomBtnsLayout);
		layout.add(bottomBtnsLayout);

		// ikony tlačítek
		upRowBtn = new ImageButton("Řádek nahoru", ImageIcon.UP_16_ICON);
		downRowBtn = new ImageButton("Řádek dolů", ImageIcon.DOWN_16_ICON);
		upPageBtn = new ImageButton("Stránka nahoru", ImageIcon.UP_16_ICON);
		downPageBtn = new ImageButton("Stránka dolů", ImageIcon.DOWN_16_ICON);
		startPageBtn = new ImageButton("Na začátek", ImageIcon.UP_16_ICON);
		endPageBtn = new ImageButton("Na konec", ImageIcon.DOWN_16_ICON);

		// listenery horních tlačítek
		upRowBtn.addClickListener(event -> galleryGridRowOffset--);
		topBtnsLayout.add(upRowBtn);

		upPageBtn.addClickListener(event -> {
			if (galleryGridRowOffset > GALLERY_GRID_ROWS) {
				galleryGridRowOffset -= GALLERY_GRID_ROWS;
			} else {
				galleryGridRowOffset = 0;
			}
		});
		topBtnsLayout.add(upPageBtn);

		startPageBtn.addClickListener(event -> galleryGridRowOffset = 0);
		topBtnsLayout.add(startPageBtn);

		// galerie
		galleryLayout.add(galleryGridLayout);

		// listenery spodních tlačítek
		downRowBtn.addClickListener(event -> galleryGridRowOffset++);
		bottomBtnsLayout.add(downRowBtn);

		downPageBtn.addClickListener(event -> {
			// kolik řádků zbývá do konce ?
			int dif = rowsSum - (GALLERY_GRID_ROWS + galleryGridRowOffset);
			if (dif > GALLERY_GRID_ROWS)
				galleryGridRowOffset += GALLERY_GRID_ROWS;
			else
				galleryGridRowOffset += dif;
		});
		bottomBtnsLayout.add(downPageBtn);

		endPageBtn.addClickListener(event -> {
			int dif = rowsSum - (GALLERY_GRID_ROWS + galleryGridRowOffset);
			galleryGridRowOffset += dif;
		});
		bottomBtnsLayout.add(endPageBtn);

		// status labels + download
		HorizontalLayout statusLabelWrapper = new HorizontalLayout();
		statusLabelWrapper.setPadding(true);
		statusLabelWrapper.add(rowStatusLabel);
		statusLabelWrapper.setWidth("100%");
		statusLabelWrapper.addClassName("bordered");
		layout.add(statusLabelWrapper);

		upload = new PGMultiUpload(galleryDir);
		upload.addFinishedListener(e -> {
			eventBus.subscribe(PGViewerPage.this);
			progressIndicatorWindow = new ProgressDialog();
			PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogallery.getContentNode().getName(),
					galleryDir, photogallery.getContentNode().getContentTagsAsStrings(),
					photogallery.getContentNode().isPublicated(), false);
			pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO,
					photogallery.getContentNode().getCreationDate());
		});

		if (coreACL.canModifyContent(photogallery.getContentNode(), getUser()))
			statusLabelWrapper.add(upload);

		ImageButton downloadZip = new ImageButton("Zabalit do ZIP", ImageIcon.PRESENT_16_ICON,
				event -> new ConfirmDialog("Přejete si vytvořit ZIP galerie?", e -> {
					logger.info("zipPhotogallery thread: {}", Thread.currentThread().getId());
					progressIndicatorWindow = new ProgressDialog();
					eventBus.subscribe(PGViewerPage.this);
					pgService.zipGallery(galleryDir);
				}).open());
		statusLabelWrapper.add(downloadZip);

		// společný listener pro všechna tlačítka
		ComponentEventListener<ClickEvent<Button>> btnCommonListener = event -> shiftGrid();
		upRowBtn.addClickListener(btnCommonListener);
		upPageBtn.addClickListener(btnCommonListener);
		startPageBtn.addClickListener(btnCommonListener);
		downRowBtn.addClickListener(btnCommonListener);
		downPageBtn.addClickListener(btnCommonListener);
		endPageBtn.addClickListener(btnCommonListener);

		refreshStatusLabel();
		populateGrid();
		checkOffsetBtnsAvailability();

		if (pgSelected != null)
			showItem(pgSelected);
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
			Runnable onDone = () -> UIUtils.redirect(getPageURL(photogalleryViewerPageFactory, URLIdentifierUtils
					.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
			if (!upload.isWarnWindowDeployed())
				onDone.run();
			else
				upload.getWarnWindow().addDialogCloseActionListener(e -> onDone.run());
		});
		eventBus.unsubscribe(PGViewerPage.this);
	}

	@Handler
	protected void onProcessStart(final PGZipProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			progressIndicatorWindow.open();
		});
	}

	@Handler
	protected void onProcessProgress(PGZipProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGZipProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();

			if (event.isSuccess()) {
				Dialog win = new Dialog();
				win.addDialogCloseActionListener(e -> pgService.deleteZipFile(event.getZipFile()));

				Anchor link = new Anchor(new StreamResource(photogallery.getPhotogalleryPath() + ".zip", () -> {
					try {
						return Files.newInputStream(event.getZipFile());
					} catch (IOException e1) {
						e1.printStackTrace();
						return null;
					}
				}), "Stáhnout ZIP souboru");
				link.setTarget("_blank");
				VerticalLayout layout = new VerticalLayout();
				layout.setSpacing(true);
				layout.setPadding(true);
				win.add(layout);
				layout.add(link);
				win.open();
			} else {
				UIUtils.showWarning(event.getResultDetails());
			}
		});
		eventBus.unsubscribe(PGViewerPage.this);
	}

	private void shiftGrid() {
		populateGrid();
		refreshStatusLabel();
		checkOffsetBtnsAvailability();
	}

	private void refreshStatusLabel() {
		rowStatusLabel.setText(ROWS_STATUS_PREFIX + galleryGridRowOffset + "-"
				+ ((rowsSum > GALLERY_GRID_ROWS ? GALLERY_GRID_ROWS : rowsSum) + galleryGridRowOffset) + "/" + rowsSum
				+ IMAGE_SUM_PREFIX + imageSum + " -- ID: " + photogallery.getPhotogalleryPath());
	}

	private void checkOffsetBtnsAvailability() {
		boolean upBtnsAvailFlag = galleryGridRowOffset > 0;
		upRowBtn.setEnabled(upBtnsAvailFlag);
		upPageBtn.setEnabled(upBtnsAvailFlag);
		startPageBtn.setEnabled(upBtnsAvailFlag);

		boolean downBtnsAvailFlag = rowsSum > GALLERY_GRID_ROWS + galleryGridRowOffset;
		downRowBtn.setEnabled(downBtnsAvailFlag);
		downPageBtn.setEnabled(downBtnsAvailFlag);
		endPageBtn.setEnabled(downBtnsAvailFlag);
	}

	private String getItemURL(String file) {
		return GrassPage.getContextPath() + "/" + PGConfiguration.PG_PATH + "/" + photogallery.getPhotogalleryPath()
				+ "/" + file;
	}

	private void populateGrid() {
		galleryGridLayout.removeAll();
		int start = galleryGridRowOffset * GALLERY_GRID_COLS;
		int limit = GALLERY_GRID_COLS * GALLERY_GRID_ROWS;
		int index = start;
		try {
			for (PhotogalleryViewItemTO item : pgService.getViewItems(galleryDir, start, limit)) {
				final int currentIndex = index;
				VerticalLayout itemLayout = new VerticalLayout();
				itemLayout.setPadding(false);
				itemLayout.setSpacing(true);

				// Miniatura/Náhled
				Image embedded = new Image(new StreamResource(item.getName(), () -> {
					try {
						return Files.newInputStream(item.getFile());
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}), item.getName());
				itemLayout.add(embedded);

				String file = item.getFile().getFileName().toString();
				String url = getItemURL(file);
				boolean video = PhotogalleryItemType.VIDEO.equals(item.getType());
				if (video)
					url = url.substring(0, url.length() - 4);
				Anchor link = new Anchor(url, video ? "Stáhnout video" : "Plné rozlišení");
				link.setTarget("_blank");
				itemLayout.add(link);

				galleryGridLayout.add(itemLayout);

				embedded.addClickListener(event -> showItem(currentIndex));

				index++;
			}
		} catch (Exception e) {
			UIUtils.showWarning("Listování galerie selhalo");
		}
	}

	private void showItem(final int index) {
		ImageSlideshowWindow window = new ImageSlideshowWindow(imageSum) {
			private static final long serialVersionUID = 7926209313704634472L;

			private Component showItem(PhotogalleryViewItemTO itemTO) {
				// zajisti posuv přehledu
				if (currentIndex > (galleryGridRowOffset + GALLERY_GRID_ROWS) * GALLERY_GRID_COLS - 1) {
					galleryGridRowOffset++;
					shiftGrid();
				} else if (currentIndex < galleryGridRowOffset * GALLERY_GRID_COLS) {
					galleryGridRowOffset--;
					shiftGrid();
				}

				// vytvoř odpovídající komponentu pro zobrazení
				// obrázku nebo videa
				switch (itemTO.getType()) {
				case VIDEO:
					return showVideo(itemTO);
				case IMAGE:
				default:
					return showImage(itemTO);
				}
			}

			@Override
			public void showItem(int index) {
				currentIndex = index;
				try {
					PhotogalleryViewItemTO itemTO = pgService.getSlideshowItem(galleryDir, index);

					Component slideshowComponent = showItem(itemTO);

					slideShowLayout.removeAll();
					slideShowLayout.add(slideshowComponent);

					itemLabel.setText((index + 1) + "/" + totalCount + " " + itemTO.getName());
				} catch (Exception e) {
					logger.error("Chyba při zobrazování slideshow položky fotogalerie", e);
					UIUtils.showWarning("Zobrazení položky se nezdařilo");
					close();
				}
			}

		};
		window.open();
		window.showItem(index);
	}

	private Component showVideo(PhotogalleryViewItemTO itemTO) {
		String videoURL = getItemURL(itemTO.getFile().getFileName().toString());
		String videoString = "<video id=\"video\" width=\"800\" height=\"600\" preload controls>" + "<source src=\""
				+ videoURL + "\" type=\"video/mp4\">" + "</video>";
		HtmlDiv video = new HtmlDiv(videoString);
		video.setWidth("800px");
		video.setHeight("600px");
		return video;
	}

	private Component showImage(PhotogalleryViewItemTO itemTO) {
		Image embedded = new Image(new StreamResource(itemTO.getName(), () -> {
			try {
				return Files.newInputStream(itemTO.getFile());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}), itemTO.getName());
		embedded.setSizeUndefined();
		return embedded;
	}

	@Override
	protected PageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmDialog confirmSubwindow = new ConfirmDialog("Opravdu si přejete smazat tuto galerii ?", ev -> {
			NodeOverviewTO nodeDTO = photogallery.getContentNode().getParent();

			final String nodeURL = getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

			// zdařilo se ? Pokud ano, otevři info okno a při
			// potvrzení jdi na kategorii
			if (pgService.deletePhotogallery(photogallery.getId())) {
				UIUtils.redirect(nodeURL);
			} else {
				// Pokud ne, otevři warn okno a při
				// potvrzení jdi na kategorii
				WarnDialog warnSubwindow = new WarnDialog("Při mazání galerie se nezdařilo smazat některé soubory.");
				warnSubwindow.addDialogCloseActionListener(e -> UIUtils.redirect(nodeURL));
				warnSubwindow.open();
			}
		});
		confirmSubwindow.open();
	}

	@Override
	protected void onEditOperation() {
		UIUtils.redirect(getPageURL(photogalleryEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
	}
}

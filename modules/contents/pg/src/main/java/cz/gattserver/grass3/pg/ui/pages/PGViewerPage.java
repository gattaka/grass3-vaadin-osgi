package cz.gattserver.grass3.pg.ui.pages;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessStartEvent;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryItemType;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.ui.windows.ImageSlideshowWindow;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.BaseProgressBar;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.ContentViewerPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.grass3.ui.windows.ProgressWindow;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.server.URLPathAnalyzer;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.WarnWindow;
import net.engio.mbassy.listener.Handler;

public class PGViewerPage extends ContentViewerPage {

	private static final Logger logger = LoggerFactory.getLogger(PGViewerPage.class);
	private static final String ROWS_STATUS_PREFIX = "Zobrazeny řádky: ";
	private static final String IMAGE_SUM_PREFIX = " | Celkem položek: ";

	@Autowired
	private PGService pgService;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Resource(name = "pgEditorPageFactory")
	private PageFactory photogalleryEditorPageFactory;

	@Autowired
	private EventBus eventBus;

	private UI ui = UI.getCurrent();
	private ProgressWindow progressIndicatorWindow;

	private PhotogalleryTO photogallery;

	private int rowsSum;
	private int imageSum;
	private int galleryGridRowOffset;
	private static final int GALLERY_GRID_COLS = 4;
	private static final int GALLERY_GRID_ROWS = 3;

	private Label rowStatusLabel;

	private Button upRowBtn;
	private Button downRowBtn;
	private Button upPageBtn;
	private Button downPageBtn;
	private Button startPageBtn;
	private Button endPageBtn;

	private GridLayout galleryGridLayout;

	/**
	 * Položka z fotogalerie, která byla dle URL vybrána (nepovinné)
	 */
	private Integer pgSelected;
	private String galleryDir;

	public PGViewerPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		rowsSum = 0;
		imageSum = 0;
		galleryGridRowOffset = 0;

		rowStatusLabel = new Label();

		upRowBtn = new Button("Řádek nahoru");
		downRowBtn = new Button("Řádek dolů");
		upPageBtn = new Button("Stránka nahoru");
		downPageBtn = new Button("Stránka dolů");
		startPageBtn = new Button("Na začátek");
		endPageBtn = new Button("Na konec");

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(analyzer.getNextPathToken());
		if (identifier == null)
			throw new GrassPageException(404);

		photogallery = pgService.getPhotogalleryForDetail(identifier.getId());
		if (photogallery == null)
			throw new GrassPageException(404);

		String magicPass = analyzer.getNextPathToken();

		if (!"MAG1CK".equals(magicPass)) {
			if (!photogallery.getContentNode().isPublicated() && (UIUtils.getUser() == null
					|| (!photogallery.getContentNode().getAuthor().equals(UIUtils.getUser())
							&& !UIUtils.getUser().isAdmin())))
				throw new GrassPageException(403);
		}

		galleryDir = photogallery.getPhotogalleryPath();

		String pgSelectedToken = analyzer.getNextPathToken();
		if (pgSelectedToken != null)
			try {
				// index je od 0, ale číslování obsahu bude od 1
				pgSelected = Integer.valueOf(pgSelectedToken) - 1;
			} catch (Exception e) {
				throw new GrassPageException(404);
			}

		return super.createPayload();
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
	protected void createContent(VerticalLayout layout) {

		// pokud je galerie porušená, pak nic nevypisuj
		try {
			if (!pgService.checkGallery(galleryDir)) {
				layout.addComponent(new Label("Chyba: Galerie je porušená -- kontaktujte administrátora (ID: "
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
		galleryLayout.addStyleName("bordered");

		galleryGridLayout = new GridLayout(GALLERY_GRID_COLS, GALLERY_GRID_ROWS);
		galleryGridLayout.setSpacing(true);
		galleryGridLayout.setMargin(true);
		galleryGridLayout.setSizeFull();

		// Horní layout tlačítek
		HorizontalLayout topBtnsLayout = new HorizontalLayout();
		configureBtnLayout(topBtnsLayout);
		layout.addComponent(topBtnsLayout);

		layout.addComponent(galleryLayout);

		// Spodní layout tlačítek
		HorizontalLayout bottomBtnsLayout = new HorizontalLayout();
		configureBtnLayout(bottomBtnsLayout);
		layout.addComponent(bottomBtnsLayout);

		// ikony tlačítek
		upRowBtn.setIcon(ImageIcon.UP_16_ICON.createResource());
		downRowBtn.setIcon(ImageIcon.DOWN_16_ICON.createResource());
		upPageBtn.setIcon(ImageIcon.UP_16_ICON.createResource());
		downPageBtn.setIcon(ImageIcon.DOWN_16_ICON.createResource());
		startPageBtn.setIcon(ImageIcon.UP_16_ICON.createResource());
		endPageBtn.setIcon(ImageIcon.DOWN_16_ICON.createResource());

		// listenery horních tlačítek
		upRowBtn.addClickListener(event -> galleryGridRowOffset--);
		topBtnsLayout.addComponent(upRowBtn);
		topBtnsLayout.setComponentAlignment(upRowBtn, Alignment.MIDDLE_CENTER);

		upPageBtn.addClickListener(event -> {
			if (galleryGridRowOffset > GALLERY_GRID_ROWS) {
				galleryGridRowOffset -= GALLERY_GRID_ROWS;
			} else {
				galleryGridRowOffset = 0;
			}
		});
		topBtnsLayout.addComponent(upPageBtn);
		topBtnsLayout.setComponentAlignment(upPageBtn, Alignment.MIDDLE_CENTER);

		startPageBtn.addClickListener(event -> galleryGridRowOffset = 0);
		topBtnsLayout.addComponent(startPageBtn);
		topBtnsLayout.setComponentAlignment(startPageBtn, Alignment.MIDDLE_CENTER);

		// galerie
		galleryLayout.addComponent(galleryGridLayout);
		galleryLayout.setComponentAlignment(galleryGridLayout, Alignment.MIDDLE_CENTER);

		// listenery spodních tlačítek
		downRowBtn.addClickListener(event -> galleryGridRowOffset++);
		bottomBtnsLayout.addComponent(downRowBtn);
		bottomBtnsLayout.setComponentAlignment(downRowBtn, Alignment.MIDDLE_CENTER);

		downPageBtn.addClickListener(event -> {
			// kolik řádků zbývá do konce ?
			int dif = rowsSum - (GALLERY_GRID_ROWS + galleryGridRowOffset);
			if (dif > GALLERY_GRID_ROWS)
				galleryGridRowOffset += GALLERY_GRID_ROWS;
			else
				galleryGridRowOffset += dif;
		});
		bottomBtnsLayout.addComponent(downPageBtn);
		bottomBtnsLayout.setComponentAlignment(downPageBtn, Alignment.MIDDLE_CENTER);

		endPageBtn.addClickListener(event -> {
			int dif = rowsSum - (GALLERY_GRID_ROWS + galleryGridRowOffset);
			galleryGridRowOffset += dif;
		});
		bottomBtnsLayout.addComponent(endPageBtn);
		bottomBtnsLayout.setComponentAlignment(endPageBtn, Alignment.MIDDLE_CENTER);

		// status labels + download
		HorizontalLayout statusLabelWrapper = new HorizontalLayout();
		statusLabelWrapper.setMargin(true);
		statusLabelWrapper.addComponent(rowStatusLabel);
		statusLabelWrapper.setComponentAlignment(rowStatusLabel, Alignment.MIDDLE_LEFT);
		statusLabelWrapper.setWidth("100%");
		statusLabelWrapper.addStyleName("bordered");
		statusLabelWrapper.setExpandRatio(rowStatusLabel, 1);
		layout.addComponent(statusLabelWrapper);

		Button downloadZip = new Button("Zabalit galerii jako ZIP",
				event -> UI.getCurrent().addWindow(new ConfirmWindow("Přejete si vytvořit ZIP galerie?", e -> {
					logger.info("zipPhotogallery thread: {}", Thread.currentThread().getId());
					eventBus.subscribe(PGViewerPage.this);
					pgService.zipGallery(galleryDir);
				})));
		statusLabelWrapper.addComponent(downloadZip);
		downloadZip.setIcon(ImageIcon.PRESENT_16_ICON.createResource());

		// společný listener pro všechna tlačítka
		Button.ClickListener btnCommonListener = event -> shiftGrid();
		upRowBtn.addClickListener(btnCommonListener);
		upPageBtn.addClickListener(btnCommonListener);
		startPageBtn.addClickListener(btnCommonListener);
		downRowBtn.addClickListener(btnCommonListener);
		downPageBtn.addClickListener(btnCommonListener);
		endPageBtn.addClickListener(btnCommonListener);

		refreshStatusLabel();
		populateGrid();
		Animator.animate(galleryGridLayout, new Css().opacity(0));
		Animator.animate(galleryGridLayout, new Css().opacity(1)).delay(200).duration(200);
		checkOffsetBtnsAvailability();

		if (pgSelected != null)
			showItem(pgSelected);
	}

	@Handler
	protected void onProcessStart(final PGZipProcessStartEvent event) {
		ui.access(() -> {
			BaseProgressBar progressBar = new BaseProgressBar(event.getCountOfStepsToDo());
			progressBar.setIndeterminate(false);
			progressBar.setValue(0f);
			progressIndicatorWindow = new ProgressWindow(progressBar);
			ui.addWindow(progressIndicatorWindow);
		});
	}

	@Handler
	protected void onProcessProgress(PGZipProcessProgressEvent event) {
		ui.access(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGZipProcessResultEvent event) {
		ui.access(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();

			if (event.isSuccess()) {
				Window win = new Window("Stáhnout") {
					private static final long serialVersionUID = -3146957611784022710L;

					@Override
					public void close() {
						super.close();
						pgService.deleteZipFile(event.getZipFile());
					}
				};
				Link link = new Link("Stáhnout ZIP souboru", new FileResource(event.getZipFile().toFile()) {
					private static final long serialVersionUID = -8702951153271074955L;

					@Override
					public String getFilename() {
						return photogallery.getPhotogalleryPath() + ".zip";
					}
				});
				link.setTargetName("_blank");
				VerticalLayout layout = new VerticalLayout();
				layout.setSpacing(true);
				layout.setMargin(true);
				win.setContent(layout);
				layout.addComponent(link);
				layout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
				win.setModal(true);
				win.center();
				ui.addWindow(win);
			} else {
				UIUtils.showWarning(event.getResultDetails());
			}
		});
		eventBus.unsubscribe(PGViewerPage.this);
	}

	private void shiftGrid() {
		populateGrid();
		refreshStatusLabel();
		Animator.animate(galleryGridLayout, new Css().opacity(0));
		Animator.animate(galleryGridLayout, new Css().opacity(1)).delay(200).duration(200);
		checkOffsetBtnsAvailability();
	}

	private void refreshStatusLabel() {
		rowStatusLabel.setValue(ROWS_STATUS_PREFIX + galleryGridRowOffset + "-"
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
		return getRequest().getContextRoot() + "/" + PGConfiguration.PG_PATH + "/" + photogallery.getPhotogalleryPath()
				+ "/" + file;
	}

	private void populateGrid() {
		galleryGridLayout.removeAllComponents();
		int start = galleryGridRowOffset * GALLERY_GRID_COLS;
		int limit = GALLERY_GRID_COLS * GALLERY_GRID_ROWS;
		int index = start;
		try {
			for (PhotogalleryViewItemTO item : pgService.getViewItems(galleryDir, start, limit)) {
				final int currentIndex = index;
				VerticalLayout itemLayout = new VerticalLayout();
				itemLayout.setMargin(false);
				itemLayout.setSpacing(true);

				// Miniatura/Náhled
				Embedded embedded = new Embedded(null, new FileResource(item.getFile().toFile()));
				itemLayout.addComponent(embedded);
				itemLayout.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);

				String file = item.getFile().getFileName().toString();
				String url = getItemURL(file);
				boolean video = PhotogalleryItemType.VIDEO.equals(item.getType());
				Link link = new Link(video ? "Stáhnout video" : "Plné rozlišení", new ExternalResource(url));
				link.setTargetName("_blank");
				itemLayout.addComponent(link);
				itemLayout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

				galleryGridLayout.addComponent(itemLayout);
				galleryGridLayout.setComponentAlignment(itemLayout, Alignment.MIDDLE_CENTER);

				embedded.addClickListener(event -> showItem(currentIndex));

				index++;
			}
		} catch (Exception e) {
			UIUtils.showWarning("Listování galerie selhalo");
		}
	}

	private void showItem(final int index) {
		ImageSlideshowWindow window = new ImageSlideshowWindow(galleryDir, imageSum) {
			private static final long serialVersionUID = 7926209313704634472L;

			@Override
			protected Component showItem(PhotogalleryViewItemTO itemTO) {
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
		};
		UI.getCurrent().addWindow(window);
		window.showItem(index);
	}

	private Component showVideo(PhotogalleryViewItemTO itemTO) {
		String videoURL = getItemURL(itemTO.getFile().getFileName().toString());
		String videoString = "<video id=\"video\" width=\"800\" height=\"600\" preload controls>" + "<source src=\""
				+ videoURL + "\" type=\"video/mp4\">" + "</video>";
		Label video = new Label(videoString, ContentMode.HTML);
		video.setWidth("800px");
		video.setHeight("600px");
		return video;
	}

	private Component showImage(PhotogalleryViewItemTO itemTO) {
		Embedded embedded = new Embedded(null, new FileResource(itemTO.getFile().toFile()));
		embedded.setSizeUndefined();
		return embedded;
	}

	@Override
	protected PageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmWindow confirmSubwindow = new ConfirmWindow("Opravdu si přejete smazat tuto galerii ?", ev -> {
			NodeOverviewTO nodeDTO = photogallery.getContentNode().getParent();

			final String nodeURL = getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

			// zdařilo se ? Pokud ano, otevři info okno a při
			// potvrzení jdi na kategorii
			try {
				pgService.deletePhotogallery(photogallery.getId());
				UIUtils.redirect(nodeURL);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno a při
				// potvrzení jdi na kategorii
				logger.error("Během mazání galerie došlo k chybě", e);
				WarnWindow warnSubwindow = new WarnWindow("Smazání galerie se nezdařilo.");
				UI.getCurrent().addWindow(warnSubwindow);
			}
		});
		UI.getCurrent().addWindow(confirmSubwindow);
	}

	@Override
	protected void onEditOperation() {
		UIUtils.redirect(getPageURL(photogalleryEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
	}
}

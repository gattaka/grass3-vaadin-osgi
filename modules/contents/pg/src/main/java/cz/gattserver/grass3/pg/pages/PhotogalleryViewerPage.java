package cz.gattserver.grass3.pg.pages;

import java.io.File;
import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import cz.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.events.PGZipProcessProgressEvent;
import cz.gattserver.grass3.pg.events.PGZipProcessResultEvent;
import cz.gattserver.grass3.pg.events.PGZipProcessStartEvent;
import cz.gattserver.grass3.pg.facade.PhotogalleryFacade;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.ui.components.BaseProgressBar;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.ContentViewerPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.grass3.ui.windows.ProgressWindow;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.URLPathAnalyzer;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.WarnWindow;
import cz.gattserver.web.common.window.WebWindow;
import net.engio.mbassy.listener.Handler;

public class PhotogalleryViewerPage extends ContentViewerPage {

	@Autowired
	private PhotogalleryFacade photogalleryFacade;

	@Autowired
	private ConfigurationService configurationService;

	@Resource(name = "photogalleryViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "photogalleryEditorPageFactory")
	private PageFactory photogalleryEditorPageFactory;

	@Autowired
	private EventBus eventBus;

	private UI ui = UI.getCurrent();
	private ProgressWindow progressIndicatorWindow;

	private PhotogalleryDTO photogallery;

	private int rowsSum;
	private int imageSum;
	private int galleryGridRowOffset;
	private static final int GALLERY_GRID_COLS = 4;
	private static final int GALLERY_GRID_ROWS = 3;

	/**
	 * Položka z fotogalerie, která byla dle URL vybrána (nepovinné)
	 */
	private String pgSelectedVideoItemId;

	private Label rowStatusLabel;

	private Button upRowBtn;
	private Button downRowBtn;
	private Button upPageBtn;
	private Button downPageBtn;
	private Button startPageBtn;
	private Button endPageBtn;

	private static final String ROWS_STATUS_PREFIX = "Zobrazeny řádky: ";
	private static final String IMAGE_SUM_PREFIX = " | Celkový počet fotek: ";

	private GridLayout galleryGridLayout;

	private File galleryDir;
	private File photoMiniaturesDirFile;
	private File videoPreviewsDirFile;
	private File slideshowDirFile;

	/**
	 * Miniatury fotek
	 */
	private File[] photoMiniatures;
	/**
	 * Náhledy videí
	 */
	private File[] videoPreviews;

	public PhotogalleryViewerPage(GrassRequest request) {
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

		photogallery = photogalleryFacade.getPhotogalleryForDetail(identifier.getId());
		if (photogallery == null)
			throw new GrassPageException(404);

		if (photogallery.getContentNode().isPublicated()
				|| (UIUtils.getUser() != null && (photogallery.getContentNode().getAuthor().equals(UIUtils.getUser())
						|| UIUtils.getUser().isAdmin()))) {
		} else {
			throw new GrassPageException(403);
		}

		PhotogalleryConfiguration configuration = new PhotogalleryConfiguration();
		configurationService.loadConfiguration(configuration);

		galleryDir = new File(configuration.getRootDir(), photogallery.getPhotogalleryPath());
		photoMiniaturesDirFile = new File(galleryDir, configuration.getMiniaturesDir());
		videoPreviewsDirFile = new File(galleryDir, configuration.getPreviewsDir());
		slideshowDirFile = new File(galleryDir, configuration.getSlideshowDir());

		pgSelectedVideoItemId = analyzer.getNextPathToken();
		if (pgSelectedVideoItemId != null) {
			if (new File(galleryDir, pgSelectedVideoItemId).exists() == false) {
				throw new GrassPageException(404);
			}
		}

		return super.createPayload();
	}

	@Override
	protected ContentNodeTO getContentNodeDTO() {
		return photogallery.getContentNode();
	}

	private void configureBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.setSpacing(true);
		// btnLayout.addStyleName("bordered");
		btnLayout.setWidth("100%");
		// btnLayout.setMargin(true);
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		// pokud je galerie porušená, pak nic nevypisuj
		if (photoMiniaturesDirFile.exists() == false || videoPreviewsDirFile.exists() == false) {
			layout.addComponent(new Label("Chyba: Galerie je porušená -- kontaktujte administrátora (ID: "
					+ photogallery.getPhotogalleryPath() + ")"));
			return;
		}

		photoMiniatures = photoMiniaturesDirFile.listFiles();
		Arrays.sort(photoMiniatures);

		videoPreviews = videoPreviewsDirFile.listFiles();
		Arrays.sort(videoPreviews);

		imageSum = photoMiniatures.length + videoPreviews.length;
		rowsSum = (int) Math.ceil((photoMiniatures.length + videoPreviews.length) * 1f / GALLERY_GRID_COLS);

		VerticalLayout galleryLayout = new VerticalLayout();
		galleryLayout.setSpacing(true);
		galleryLayout.addStyleName("bordered");

		galleryGridLayout = new GridLayout(GALLERY_GRID_COLS, GALLERY_GRID_ROWS);
		galleryGridLayout.setSpacing(true);
		galleryGridLayout.setMargin(true);
		galleryGridLayout.setWidth("700px");
		galleryGridLayout.setHeight("550px");

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
			if (dif > GALLERY_GRID_ROWS) {
				galleryGridRowOffset += GALLERY_GRID_ROWS;
			} else {
				galleryGridRowOffset += dif;
			}
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
		statusLabelWrapper.setComponentAlignment(rowStatusLabel, Alignment.MIDDLE_CENTER);
		statusLabelWrapper.setWidth("100%");
		statusLabelWrapper.addStyleName("bordered");
		statusLabelWrapper.setExpandRatio(rowStatusLabel, 1);
		layout.addComponent(statusLabelWrapper);

		Button downloadZip = new Button("Zabalit galerii jako ZIP",
				event -> UI.getCurrent().addWindow(new ConfirmWindow("Přejete si vytvořit ZIP galerie?", e -> {
					System.out.println("zipPhotogallery thread: " + Thread.currentThread().getId());
					eventBus.subscribe(PhotogalleryViewerPage.this);
					ui.setPollInterval(200);
					photogalleryFacade.zipGallery(galleryDir);
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
		populateGrid(photoMiniatures, videoPreviews);
		Animator.animate(galleryGridLayout, new Css().opacity(0));
		Animator.animate(galleryGridLayout, new Css().opacity(1)).delay(200).duration(200);
		checkOffsetBtnsAvailability();

		if (pgSelectedVideoItemId != null) {
			if (PGUtils.isVideo(pgSelectedVideoItemId)) {
				showVideo(pgSelectedVideoItemId, getItemURL(pgSelectedVideoItemId));
			}
		}
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
			// ui.setPollInterval(-1);
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.closeOnDone();

			if (event.isSuccess()) {
				ui.addWindow(new Window("Stáhnout") {
					private static final long serialVersionUID = -3146957611784022710L;

					{
						Link link = new Link("Stáhnout ZIP souboru", new FileResource(event.getZipFile()) {
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
						setContent(layout);
						layout.addComponent(link);
						layout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
						setModal(true);
						center();
					}

					@Override
					public void close() {
						super.close();
						event.getZipFile().delete();
					}
				});

			} else {
				UIUtils.showWarning(event.getResultDetails());
			}
		});
		eventBus.unsubscribe(PhotogalleryViewerPage.this);
	}

	private void shiftGrid() {
		populateGrid(photoMiniatures, videoPreviews);
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

	private void showVideo(String videoName, String videoURL) {
		UI.getCurrent().addWindow(new WebWindow(videoName) {
			private static final long serialVersionUID = 5027839567542107630L;

			{
				String videoString = "<video id=\"video\" width=\"800\" height=\"600\" preload controls>"
						+ "<source src=\"" + videoURL + "\" type=\"video/mp4\">" + "</video>";

				Label video = new Label(videoString, ContentMode.HTML);

				video.setWidth("800px");
				video.setHeight("600px");
				addComponent(video);
			}
		});
	}

	private void showImage(File[] miniatures, int index) {
		UI.getCurrent().addWindow(new ImageDetailWindow(miniatures, index, slideshowDirFile) {
			private static final long serialVersionUID = 5403584860186673877L;

			@Override
			protected void changeImage(int index) {
				super.changeImage(index);

				if (index > (galleryGridRowOffset + GALLERY_GRID_ROWS) * GALLERY_GRID_COLS - 1) {
					galleryGridRowOffset++;
					shiftGrid();
				} else if (index < galleryGridRowOffset * GALLERY_GRID_COLS) {
					galleryGridRowOffset--;
					shiftGrid();
				}
			}
		});
	}

	private String getItemURL(String itemId) {
		return getRequest().getContextRoot() + PhotogalleryConfiguration.PHOTOGALLERY_PATH + "/"
				+ photogallery.getPhotogalleryPath() + "/" + itemId;
	}

	private void populateGrid(final File[] miniatures, final File[] previews) {

		galleryGridLayout.removeAllComponents();

		int start = galleryGridRowOffset * GALLERY_GRID_COLS;
		int limit = Math.min(miniatures.length + previews.length, GALLERY_GRID_COLS * GALLERY_GRID_ROWS + start);
		for (int i = start; i < limit; i++) {

			// vypisuji fotky nebo už videa?
			boolean videos = i >= miniatures.length;
			int videoIndex = i - miniatures.length;
			final int index = i;

			int gridIndex = i - start;

			VerticalLayout itemLayout = new VerticalLayout();
			itemLayout.setSpacing(true);

			// Image
			final File miniature = videos ? previews[videoIndex] : miniatures[i];
			Embedded embedded = new Embedded(null, new FileResource(miniature));
			itemLayout.addComponent(embedded);
			itemLayout.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);

			String itemId = videos ? miniature.getName().substring(0, miniature.getName().length() - 4)
					: miniature.getName();

			// odeber ".png"
			final String url = getItemURL(itemId);

			Link link = new Link(videos ? "Stáhnout video" : "Plné rozlišení", new ExternalResource(url));
			link.setTargetName("_blank");
			itemLayout.addComponent(link);
			itemLayout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

			galleryGridLayout.addComponent(itemLayout, gridIndex % GALLERY_GRID_COLS, gridIndex / GALLERY_GRID_COLS);
			galleryGridLayout.setComponentAlignment(itemLayout, Alignment.MIDDLE_CENTER);

			embedded.addClickListener(event -> {
				if (videos) {
					showVideo(itemId, url);
				} else {
					showImage(miniatures, index);
				}
			});
		}
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
				photogalleryFacade.deletePhotogallery(photogallery);
				UIUtils.redirect(nodeURL);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno a při
				// potvrzení jdi na kategorii
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

package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
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
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.ui.components.DefaultContentOperations;
import cz.gattserver.grass3.ui.components.button.ImageButton;
import cz.gattserver.grass3.ui.dialogs.ImageSlideshowDialog;
import cz.gattserver.grass3.ui.dialogs.ProgressDialog;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.ContentViewerPage;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.Breakline;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.WarnDialog;
import net.engio.mbassy.listener.Handler;

@Route("photogallery")
public class PGViewerPage extends ContentViewerPage implements HasUrlParameter<String>, HasDynamicTitle {

	private static final long serialVersionUID = 7334408385869747381L;

	private static final Logger logger = LoggerFactory.getLogger(PGViewerPage.class);

	private static final int GALLERY_GRID_COLS = 4;
	private static final int GALLERY_GRID_ROWS = 3;
	private static final int MAX_PAGE_RADIUS = 3;
	private static final int PAGE_SIZE = GALLERY_GRID_COLS * GALLERY_GRID_ROWS;

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

	private int imageCount;
	private int pageCount;
	private int currentPage = 0;

	private PGMultiUpload upload;
	private GridLayout galleryGridLayout;
	private HorizontalLayout pagingLayout;

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
	protected void createContentOperations(Div operationsListLayout) {
		super.createContentOperations(operationsListLayout);

		ImageButton downloadZip = new ImageButton("Zabalit do ZIP", ImageIcon.PRESENT_16_ICON,
				event -> new ConfirmDialog("Přejete si vytvořit ZIP galerie?", e -> {
					logger.info("zipPhotogallery thread: {}", Thread.currentThread().getId());
					progressIndicatorWindow = new ProgressDialog();
					eventBus.subscribe(PGViewerPage.this);
					pgService.zipGallery(galleryDir);
				}).open());
		operationsListLayout.add(downloadZip);
	}

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		if (chunks.length > 0)
			identifierToken = chunks[0];
		if (chunks.length > 1)
			magickToken = chunks[1];

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null)
			throw new GrassPageException(404);

		photogallery = pgService.getPhotogalleryForDetail(identifier.getId());
		if (photogallery == null)
			throw new GrassPageException(404);

		if (!"MAG1CK".equals(magickToken) && !photogallery.getContentNode().isPublicated() && !isAdminOrAuthor())
			throw new GrassPageException(403);

		galleryDir = photogallery.getPhotogalleryPath();

		loadJS();
		init();
	}

	private boolean isAdminOrAuthor() {
		return getUser().isAdmin() || photogallery.getContentNode().getAuthor().equals(getUser());
	}

	@Override
	protected ContentNodeTO getContentNodeDTO() {
		return photogallery.getContentNode();
	}

	@Override
	protected void createContent(Div layout) {
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
			imageCount = pgService.getViewItemsCount(photogallery.getPhotogalleryPath());
		} catch (Exception e) {
			throw new GrassPageException(500, e);
		}
		pageCount = (int) Math.ceil((double) imageCount / PAGE_SIZE);

		// galerie
		galleryGridLayout = new GridLayout();
		galleryGridLayout.setHeightFull();
		layout.add(galleryGridLayout);

		// Layout stránkovacích tlačítek
		pagingLayout = new HorizontalLayout();
		pagingLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		pagingLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		pagingLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		pagingLayout.setSpacing(true);
		pagingLayout.setPadding(false);
		layout.add(pagingLayout);

		upload = new PGMultiUpload(galleryDir);
		Button uploadButton = new Button("Upload");
		upload.setUploadButton(uploadButton);
		Span dropLabel = new Span("Drop");
		upload.setDropLabel(dropLabel);
		upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
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
			layout.add(upload);

		Div statusRow = new Div();
		statusRow.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		statusRow.getStyle().set("background", "#fdfaf2").set("padding", "3px 10px").set("line-height", "20px")
				.set("font-size", "12px").set("color", "#777");
		statusRow.setSizeUndefined();
		statusRow.setText("Galerie: " + photogallery.getPhotogalleryPath() + " celkem položek: " + imageCount);
		layout.add(statusRow);

		refreshGrid();

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
			Runnable onDone = () -> UI.getCurrent().getPage().reload();
			if (!upload.isWarnWindowDeployed())
				onDone.run();
			else
				upload.getWarnWindow().addDetachListener(e -> onDone.run());
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

	private String getItemURL(String file) {
		return GrassPage.getContextPath() + "/" + PGConfiguration.PG_PATH + "/" + photogallery.getPhotogalleryPath()
				+ "/" + file;
	}

	private void refreshGrid() {
		galleryGridLayout.removeAll();
		int start = currentPage * PAGE_SIZE;
		int index = start;
		try {
			int counter = 0;
			for (PhotogalleryViewItemTO item : pgService.getViewItems(galleryDir, start, PAGE_SIZE)) {
				if (counter == 0)
					galleryGridLayout.newRow();
				counter = (counter + 1) % 4;

				final int currentIndex = index;
				Div itemLayout = new Div();
				itemLayout.getStyle().set("text-align", "center").set("width", "170px");

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

				itemLayout.add(new Breakline());

				// Detail
				String file = item.getFile().getFileName().toString();
				String url = getItemURL(file);
				boolean video = PhotogalleryItemType.VIDEO.equals(item.getType());
				if (video) {
					url = url.substring(0, url.length() - 4);
				} else if (url.endsWith(".svg.png")) {
					// U vektorů je potřeba uříznout .png příponu, protože
					// originál je vektor, který se na slideshow dá rovnou
					// použít
					url = url.substring(0, url.length() - 4);
				}
				Anchor link = new Anchor(url, "Detail");
				link.addClassName(UIUtils.BUTTON_LINK_CSS_CLASS);
				link.setTarget("_blank");
				itemLayout.add(link);

				// Smazat
				if (coreACL.canModifyContent(photogallery.getContentNode(), getUser())) {
					// TODO
				}

				galleryGridLayout.add(itemLayout);

				embedded.addClickListener(event -> showItem(currentIndex));

				index++;
			}
			pagingLayout.removeAll();
			if (pageCount > 8) {
				Button btn = new Button("1", e -> setPage(0));
				pagingLayout.add(btn);
				if (currentPage == 0)
					btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				int pageRadius = Math.min(MAX_PAGE_RADIUS, pageCount / 2 + 1);
				int startPage = Math.max(1, currentPage - pageRadius);
				int endPage = Math.min(currentPage + pageRadius, pageCount - 2);
				if (startPage <= endPage) {
					if (startPage > 1)
						pagingLayout.add(new Span("..."));
					for (int i = startPage; i <= endPage; i++) {
						int page = i;
						btn = new Button(String.valueOf(i + 1), e -> setPage(page));
						if (currentPage == page)
							btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
						pagingLayout.add(btn);
					}
					if (endPage < pageCount - 2)
						pagingLayout.add(new Span("..."));
					btn = new Button(String.valueOf(pageCount), e -> setPage(pageCount - 1));
					pagingLayout.add(btn);
					if (currentPage == pageCount - 1)
						btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				}
			} else {
				for (int i = 1; i <= pageCount; i++) {
					int page = i - 1;
					Button btn = new Button(String.valueOf(i), e -> setPage(page));
					if (currentPage == page)
						btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
					pagingLayout.add(btn);
				}
			}
		} catch (Exception e) {
			UIUtils.showWarning("Listování galerie selhalo");
		}
	}

	private void setPage(int page) {
		if (page == currentPage)
			return;
		currentPage = page;
		refreshGrid();
	}

	private void showItem(final int index) {
		ImageSlideshowDialog window = new ImageSlideshowDialog(imageCount) {
			private static final long serialVersionUID = 7926209313704634472L;

			private Component showItem(PhotogalleryViewItemTO itemTO) {
				// zajisti posuv přehledu
				int newPage = currentIndex / PAGE_SIZE;
				if (newPage != currentPage) {
					currentPage = newPage;
					refreshGrid();
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
		embedded.getStyle().set("max-width", PGUtils.SLIDESHOW_WIDTH + "px").set("max-height",
				PGUtils.SLIDESHOW_HEIGHT + "px");
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

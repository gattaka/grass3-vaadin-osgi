package cz.gattserver.grass3.pg.pages;

import java.io.File;
import java.util.Arrays;

import javax.annotation.Resource;

import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.shared.AnimType;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.config.IConfigurationService;
import cz.gattserver.grass3.facades.INodeFacade;
import cz.gattserver.grass3.facades.IUserFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pages.template.ContentViewerPage;
import cz.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.URLPathAnalyzer;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.InfoWindow;
import cz.gattserver.web.common.window.WarnWindow;
import cz.gattserver.web.common.window.WebWindow;

public class PhotogalleryViewerPage extends ContentViewerPage {

	private static final long serialVersionUID = 5078280973817331002L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "photogalleryFacade")
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource
	private IConfigurationService configurationService;

	@Resource(name = "photogalleryViewerPageFactory")
	private IPageFactory photogalleryViewerPageFactory;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "photogalleryEditorPageFactory")
	private IPageFactory photogalleryEditorPageFactory;

	private PhotogalleryDTO photogallery;

	private int rowsSum;
	private int imageSum;
	private int galleryGridRowOffset;
	private int galleryGridCols;
	private int galleryGridRows;

	private Label rowStatusLabel;

	private Button upRowBtn;
	private Button downRowBtn;
	private Button upPageBtn;
	private Button downPageBtn;
	private Button startPageBtn;
	private Button endPageBtn;

	private String ROWS_STATUS_PREFIX;
	private String IMAGE_SUM_PREFIX;

	private GridLayout galleryGridLayout;

	private File galleryDir;
	private File miniaturesDirFile;
	private File previewsDirFile;
	private File slideshowDirFile;

	public PhotogalleryViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {

		rowsSum = 0;
		imageSum = 0;
		galleryGridRowOffset = 0;
		galleryGridCols = 4;
		galleryGridRows = 3;

		rowStatusLabel = new Label();

		upRowBtn = new Button("Řádek nahoru");
		downRowBtn = new Button("Řádek dolů");
		upPageBtn = new Button("Stránka nahoru");
		downPageBtn = new Button("Stránka dolů");
		startPageBtn = new Button("Na začátek");
		endPageBtn = new Button("Na konec");

		ROWS_STATUS_PREFIX = "Zobrazeny řádky: ";
		IMAGE_SUM_PREFIX = " | Celkový počet fotek: ";

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(analyzer
				.getCurrentPathToken());
		if (identifier == null) {
			showError404();
			return;
		}

		photogallery = photogalleryFacade.getPhotogalleryForDetail(identifier.getId());
		if (photogallery == null) {
			showError404();
			return;
		}

		if (photogallery.getContentNode().isPublicated()
				|| (getUser() != null && (photogallery.getContentNode().getAuthor().equals(getUser()) || getUser()
						.getRoles().contains(Role.ADMIN)))) {
		} else {
			showError403();
			return;
		}

		super.init();
	}

	@Override
	protected ContentNodeDTO getContentNodeDTO() {
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

		PhotogalleryConfiguration configuration = new PhotogalleryConfiguration();
		configurationService.loadConfiguration(configuration);

		galleryDir = new File(configuration.getRootDir(), photogallery.getPhotogalleryPath());
		miniaturesDirFile = new File(galleryDir, configuration.getMiniaturesDir());
		previewsDirFile = new File(galleryDir, configuration.getPreviewsDir());
		slideshowDirFile = new File(galleryDir, configuration.getSlideshowDir());

		// pokud je galerie porušená, pak nic nevypisuj
		if (miniaturesDirFile.exists() == false || previewsDirFile.exists() == false) {
			layout.addComponent(new Label("Chyba: Galerie je porušená -- kontaktujte administrátora"));
			return;
		}

		final File[] miniatures = miniaturesDirFile.listFiles();
		Arrays.sort(miniatures);

		final File[] previews = previewsDirFile.listFiles();
		Arrays.sort(previews);

		imageSum = miniatures.length + previews.length;
		rowsSum = (int) Math.ceil((miniatures.length + previews.length) * 1f / galleryGridCols);

		VerticalLayout galleryLayout = new VerticalLayout();
		galleryLayout.setSpacing(true);
		galleryLayout.addStyleName("bordered");

		final AnimatorProxy animatorProxy = new AnimatorProxy();

		galleryGridLayout = new GridLayout(galleryGridCols, galleryGridRows);
		galleryGridLayout.setSpacing(true);
		galleryGridLayout.setMargin(true);
		galleryGridLayout.setWidth("700px");
		galleryGridLayout.setHeight("550px");
		// for (int i=0; i < galleryGridLayout.getRows(); i++) {
		// galleryGridLayout.setRowExpandRatio(rowIndex, ratio)
		// }

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
		upRowBtn.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/up_16.png"));
		downRowBtn.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/down_16.png"));
		upPageBtn.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/up_16.png"));
		downPageBtn.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/down_16.png"));
		startPageBtn.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/up_16.png"));
		endPageBtn.setIcon((com.vaadin.server.Resource) new ThemeResource("img/tags/down_16.png"));

		// listenery horních tlačítek
		upRowBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6985989532144363433L;

			@Override
			public void buttonClick(ClickEvent event) {
				galleryGridRowOffset--;
			}
		});
		topBtnsLayout.addComponent(upRowBtn);
		topBtnsLayout.setComponentAlignment(upRowBtn, Alignment.MIDDLE_CENTER);

		upPageBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6985989532144363433L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (galleryGridRowOffset > galleryGridRows) {
					galleryGridRowOffset -= galleryGridRows;
				} else {
					galleryGridRowOffset = 0;
				}
			}
		});
		topBtnsLayout.addComponent(upPageBtn);
		topBtnsLayout.setComponentAlignment(upPageBtn, Alignment.MIDDLE_CENTER);

		startPageBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6985989532144363433L;

			@Override
			public void buttonClick(ClickEvent event) {
				galleryGridRowOffset = 0;
			}
		});
		topBtnsLayout.addComponent(startPageBtn);
		topBtnsLayout.setComponentAlignment(startPageBtn, Alignment.MIDDLE_CENTER);

		// galerie
		galleryLayout.addComponent(galleryGridLayout);
		galleryLayout.setComponentAlignment(galleryGridLayout, Alignment.MIDDLE_CENTER);
		galleryLayout.addComponent(animatorProxy);

		// listenery spodních tlačítek
		downRowBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6985989532144363433L;

			@Override
			public void buttonClick(ClickEvent event) {
				galleryGridRowOffset++;
			}
		});
		bottomBtnsLayout.addComponent(downRowBtn);
		bottomBtnsLayout.setComponentAlignment(downRowBtn, Alignment.MIDDLE_CENTER);

		downPageBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6985989532144363433L;

			@Override
			public void buttonClick(ClickEvent event) {
				// kolik řádků zbývá do konce ?
				int dif = rowsSum - (galleryGridRows + galleryGridRowOffset);
				if (dif > galleryGridRows) {
					galleryGridRowOffset += galleryGridRows;
				} else {
					galleryGridRowOffset += dif;
				}
			}
		});
		bottomBtnsLayout.addComponent(downPageBtn);
		bottomBtnsLayout.setComponentAlignment(downPageBtn, Alignment.MIDDLE_CENTER);

		endPageBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6985989532144363433L;

			@Override
			public void buttonClick(ClickEvent event) {
				int dif = rowsSum - (galleryGridRows + galleryGridRowOffset);
				galleryGridRowOffset += dif;
			}
		});
		bottomBtnsLayout.addComponent(endPageBtn);
		bottomBtnsLayout.setComponentAlignment(endPageBtn, Alignment.MIDDLE_CENTER);

		// status labels
		HorizontalLayout statusLabelWrapper = new HorizontalLayout();
		statusLabelWrapper.setMargin(true);
		statusLabelWrapper.addComponent(rowStatusLabel);
		statusLabelWrapper.setComponentAlignment(rowStatusLabel, Alignment.BOTTOM_RIGHT);
		statusLabelWrapper.setWidth("100%");
		statusLabelWrapper.addStyleName("bordered");
		layout.addComponent(statusLabelWrapper);

		// společný listener pro všechna tlačítka
		Button.ClickListener btnCommonListener = new Button.ClickListener() {
			private static final long serialVersionUID = -79498882279825509L;

			@Override
			public void buttonClick(ClickEvent event) {
				populateGrid(miniatures, previews);
				refreshStatusLabel();
				animatorProxy.animate(galleryGridLayout, AnimType.FADE_IN).setDuration(200).setDelay(200);
				checkOffsetBtnsAvailability();
			}
		};

		upRowBtn.addClickListener(btnCommonListener);
		upPageBtn.addClickListener(btnCommonListener);
		startPageBtn.addClickListener(btnCommonListener);
		downRowBtn.addClickListener(btnCommonListener);
		downPageBtn.addClickListener(btnCommonListener);
		endPageBtn.addClickListener(btnCommonListener);

		refreshStatusLabel();
		populateGrid(miniatures, previews);
		animatorProxy.animate(galleryGridLayout, AnimType.FADE_IN).setDuration(200).setDelay(200);
		checkOffsetBtnsAvailability();
	}

	private void refreshStatusLabel() {
		rowStatusLabel.setValue(ROWS_STATUS_PREFIX + galleryGridRowOffset + "-"
				+ ((rowsSum > galleryGridRows ? galleryGridRows : rowsSum) + galleryGridRowOffset) + "/" + rowsSum
				+ IMAGE_SUM_PREFIX + imageSum + " -- ID: " + photogallery.getPhotogalleryPath());
	}

	private void checkOffsetBtnsAvailability() {
		boolean upBtnsAvailFlag = galleryGridRowOffset > 0;
		upRowBtn.setEnabled(upBtnsAvailFlag);
		upPageBtn.setEnabled(upBtnsAvailFlag);
		startPageBtn.setEnabled(upBtnsAvailFlag);

		boolean downBtnsAvailFlag = rowsSum > galleryGridRows + galleryGridRowOffset;
		downRowBtn.setEnabled(downBtnsAvailFlag);
		downPageBtn.setEnabled(downBtnsAvailFlag);
		endPageBtn.setEnabled(downBtnsAvailFlag);
	}

	private void populateGrid(final File[] miniatures, final File[] previews) {

		galleryGridLayout.removeAllComponents();

		int start = galleryGridRowOffset * galleryGridCols;
		int limit = Math.min(miniatures.length + previews.length, galleryGridCols * galleryGridRows + start);
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

			// HD link
			String tmpUrl = getRequest().getContextRoot() + PhotogalleryConfiguration.PHOTOGALLERY_PATH + "/"
					+ photogallery.getPhotogalleryPath() + "/" + miniature.getName();

			// odeber ".png"
			final String url = videos ? tmpUrl.substring(0, tmpUrl.length() - 4) : tmpUrl;

			Link link = new Link(videos ? "Stáhnout video" : "Plné rozlišení", new ExternalResource(url));
			link.setTargetName("_blank");
			itemLayout.addComponent(link);
			itemLayout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);

			galleryGridLayout.addComponent(itemLayout, gridIndex % galleryGridCols, gridIndex / galleryGridCols);
			galleryGridLayout.setComponentAlignment(itemLayout, Alignment.MIDDLE_CENTER);

			embedded.addClickListener(new MouseEvents.ClickListener() {
				private static final long serialVersionUID = -6354607057332715984L;

				@Override
				public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
					if (videos) {
						UI.getCurrent().addWindow(
								new WebWindow(miniature.getName().substring(0, miniature.getName().length() - 4)) {
									private static final long serialVersionUID = 5027839567542107630L;

									{
										// String videoString =
										// "<video controls=\"controls\" width=\"800\" height=\"600\" name=\"Video Name\" src=\""
										// + url + "\"></video>";

										String videoString = "<video id=\"video\" width=\"800\" height=\"600\" preload controls>"
												+ "<source src=\""
												+ url
												+ "\" type='video/mp4; codecs=\"avc1.42E01E, mp4a.40.2\"' />"
												// +"<source src=\"HTML5Sample_Ogg.ogv\" type='video/ogg; codecs=\"theora, vorbis\"' />"
												// +"<source src=\"HTML5Sample_WebM.webm\" type='video/webm; codecs=\"vp8, vorbis\"' />"
												// +"<object type=\"application/x-shockwave-flash\" data=\"http://releases.flowplayer.org/swf/flowplayer-3.2.1.swf\" width=\"800\" height=\"600\">"
												// +"<param name=\"movie\" value=\"http://releases.flowplayer.org/swf/flowplayer-3.2.1.swf\" />"
												// +"<param name=\"allowFullScreen\" value=\"true\" />"
												// +"<param name=\"wmode\" value=\"transparent\" />"
												// +"<param name=\"flashvars\" value='config={\"clip\":{\"url\":\"HTML5Sample_flv.flv\",\"autoPlay\":false,\"autoBuffering\":true}}' />"
												// +"</object>"
												+ "</video>";

										Label video = new Label(videoString, ContentMode.HTML);

										video.setWidth("800px");
										video.setHeight("600px");
										addComponent(video);
									}
								});
					} else {
						UI.getCurrent().addWindow(new ImageDetailWindow(miniatures, index, slideshowDirFile));
					}
				}
			});
		}
	}

	@Override
	protected IPageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmWindow confirmSubwindow = new ConfirmWindow("Opravdu si přejete smazat tuto galerii ?") {

			private static final long serialVersionUID = -3214040983143363831L;

			@Override
			protected void onConfirm(ClickEvent event) {

				NodeDTO node = photogallery.getContentNode().getParent();

				final String category = getPageURL(categoryPageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));

				// zdařilo se ? Pokud ano, otevři info okno a při
				// potvrzení jdi na kategorii
				try {
					photogalleryFacade.deletePhotogallery(photogallery);
					InfoWindow infoSubwindow = new InfoWindow("Smazání galerie proběhlo úspěšně.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(category);
						};
					};
					getUI().addWindow(infoSubwindow);
				} catch (Exception e) {
					// Pokud ne, otevři warn okno a při
					// potvrzení jdi na kategorii
					WarnWindow warnSubwindow = new WarnWindow("Smazání galerie se nezdařilo.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(category);
						};
					};
					getUI().addWindow(warnSubwindow);
				}

				// zavři původní confirm okno
				getUI().removeWindow(this);

			}
		};
		getUI().addWindow(confirmSubwindow);
	}

	@Override
	protected void onEditOperation() {
		redirect(getPageURL(photogalleryEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
	}
}

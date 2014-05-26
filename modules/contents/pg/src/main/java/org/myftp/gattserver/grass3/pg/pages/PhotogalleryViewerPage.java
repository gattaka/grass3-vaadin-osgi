package org.myftp.gattserver.grass3.pg.pages;

import java.io.File;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.ContentViewerPage;
import org.myftp.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;
import org.myftp.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubWindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.subwindows.WarnSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.springframework.context.annotation.Scope;
import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.shared.AnimType;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("photogalleryViewerPage")
@Scope("prototype")
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

	private int rowsSum = 0;
	private int imageSum = 0;
	private int galleryGridRowOffset = 0;
	private final int galleryGridCols = 4;
	private final int galleryGridRows = 3;

	private final Label rowStatusLabel = new Label();

	private final Button upRowBtn = new Button("Řádek nahoru");
	private final Button downRowBtn = new Button("Řádek dolů");
	private final Button upPageBtn = new Button("Stránka nahoru");
	private final Button downPageBtn = new Button("Stránka dolů");
	private final Button startPageBtn = new Button("Na začátek");
	private final Button endPageBtn = new Button("Na konec");

	private final String ROWS_STATUS_PREFIX = "Zobrazeny řádky: ";
	private final String IMAGE_SUM_PREFIX = " | Celkový počet fotek: ";

	private GridLayout galleryGridLayout;

	public PhotogalleryViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {
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

		File galleryDir = new File(configuration.getRootDir(), photogallery.getPhotogalleryPath());

		File miniaturesDirFile = new File(galleryDir, configuration.getMiniaturesDir());

		if (miniaturesDirFile.exists() == false) {
			showError404();
			return;
		}

		final File[] miniatures = miniaturesDirFile.listFiles();
		imageSum = miniatures.length;
		rowsSum = (int) Math.ceil(miniatures.length * 1f / galleryGridCols);

		VerticalLayout galleryLayout = new VerticalLayout();
		galleryLayout.setSpacing(true);
		galleryLayout.addStyleName("bordered");

		final AnimatorProxy animatorProxy = new AnimatorProxy();

		galleryGridLayout = new GridLayout(galleryGridCols, galleryGridRows);
		galleryGridLayout.setSpacing(true);
		galleryGridLayout.setMargin(true);
		galleryGridLayout.setWidth("700px");
		galleryGridLayout.setHeight("500px");
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
				populateGrid(miniatures);
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
		populateGrid(miniatures);
		animatorProxy.animate(galleryGridLayout, AnimType.FADE_IN).setDuration(200).setDelay(200);
		checkOffsetBtnsAvailability();
	}

	private void refreshStatusLabel() {
		rowStatusLabel.setValue(ROWS_STATUS_PREFIX + galleryGridRowOffset + "-"
				+ ((rowsSum > galleryGridRows ? galleryGridRows : rowsSum) + galleryGridRowOffset) + "/" + rowsSum
				+ IMAGE_SUM_PREFIX + imageSum);
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

	private void populateGrid(final File[] miniatures) {

		galleryGridLayout.removeAllComponents();

		int start = galleryGridRowOffset * galleryGridCols;
		int limit = Math.min(miniatures.length, galleryGridCols * galleryGridRows + start);
		for (int i = start; i < limit; i++) {

			final int index = i;
			int gridIndex = i - start;

			final File miniature = miniatures[i];
			Embedded embedded = new Embedded(null, new FileResource(miniature));
			galleryGridLayout.addComponent(embedded, gridIndex % galleryGridCols, gridIndex / galleryGridCols);
			galleryGridLayout.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);

			embedded.addClickListener(new MouseEvents.ClickListener() {
				private static final long serialVersionUID = -6354607057332715984L;

				@Override
				public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
					UI.getCurrent().addWindow(new ImageDetailWindow(miniatures, index));
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
		ConfirmSubWindow confirmSubwindow = new ConfirmSubWindow("Opravdu si přejete smazat tuto galerii ?") {

			private static final long serialVersionUID = -3214040983143363831L;

			@Override
			protected void onConfirm(ClickEvent event) {

				NodeDTO node = photogallery.getContentNode().getParent();

				final String category = getPageURL(categoryPageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));

				// zdařilo se ? Pokud ano, otevři info okno a při
				// potvrzení jdi na kategorii
				if (photogalleryFacade.deletePhotogallery(photogallery)) {
					InfoSubwindow infoSubwindow = new InfoSubwindow("Smazání galerie proběhlo úspěšně.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(category);
						};
					};
					getUI().addWindow(infoSubwindow);
				} else {
					// Pokud ne, otevři warn okno a při
					// potvrzení jdi na kategorii
					WarnSubwindow warnSubwindow = new WarnSubwindow("Smazání galerie se nezdařilo.") {

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

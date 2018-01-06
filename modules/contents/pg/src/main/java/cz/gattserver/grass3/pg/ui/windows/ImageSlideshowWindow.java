package cz.gattserver.grass3.pg.ui.windows;

import com.vaadin.event.ShortcutAction.KeyCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ImageSlideshowWindow extends WebWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	private static final Logger logger = LoggerFactory.getLogger(ImageSlideshowWindow.class);

	private transient PGService pgService;

	protected int currentIndex;
	private int totalCount;
	private String galleryDir;
	private Label itemLabel;
	private HorizontalLayout slideShowLayout;

	protected abstract Component showItem(PhotogalleryViewItemTO itemTO);

	private PGService getPGService() {
		if (pgService == null)
			pgService = SpringContextHelper.getBean(PGService.class);
		return pgService;
	}

	public ImageSlideshowWindow(final String galleryDir, int count) {
		this.totalCount = count;
		this.galleryDir = galleryDir;

		setResizable(false);
		setSizeFull();

		addStyleName("grass-image-slideshow-window");

		itemLabel = new Label();
		itemLabel.setStyleName("white-bold-label");
		itemLabel.setSizeUndefined();

		VerticalLayout itemLayout = new VerticalLayout();

		slideShowLayout = new HorizontalLayout();
		slideShowLayout.setSizeFull();

		itemLayout.addComponent(itemLabel);
		itemLayout.addComponent(itemLabel);
		itemLayout.setComponentAlignment(itemLabel, Alignment.BOTTOM_CENTER);
		itemLayout.addComponent(slideShowLayout);

		layout.addComponent(itemLayout);
		layout.setComponentAlignment(itemLayout, Alignment.MIDDLE_CENTER);
		layout.setSizeFull();

		addAction(new ShortcutListener("Prev", KeyCode.ARROW_LEFT, null) {
			private static final long serialVersionUID = -6194478959368277077L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (currentIndex > 0)
					showItem(currentIndex - 1);
			}
		});

		addAction(new ShortcutListener("Next", KeyCode.ARROW_RIGHT, null) {
			private static final long serialVersionUID = -6194478959368277077L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (currentIndex < totalCount - 1)
					showItem(currentIndex + 1);
			}
		});

		addClickListener(e -> close());

	}

	public void showItem(int index) {
		currentIndex = index;
		PGService service = getPGService();
		try {
			PhotogalleryViewItemTO itemTO = service.getSlideshowItem(galleryDir, index);

			Component slideshowComponent = showItem(itemTO);

			slideShowLayout.removeAllComponents();
			slideShowLayout.addComponent(slideshowComponent);
			slideShowLayout.setComponentAlignment(slideshowComponent, Alignment.MIDDLE_CENTER);
			slideShowLayout.setExpandRatio(slideshowComponent, 1);

			itemLabel.setValue((index + 1) + "/" + totalCount + " " + itemTO.getName());
		} catch (Exception e) {
			logger.error("Chyba při zobrazování slideshow položky fotogalerie", e);
			UIUtils.showWarning("Zobrazení položky se nezdařilo");
			close();
		}

		center();
	}

	@Override
	public void attach() {
		focus();
		super.attach();
	}
}

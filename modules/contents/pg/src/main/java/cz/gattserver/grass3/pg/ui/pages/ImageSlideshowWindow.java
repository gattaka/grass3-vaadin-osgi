package cz.gattserver.grass3.pg.ui.pages;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ImageSlideshowWindow extends WebWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	private transient PGService pgService;

	private int currentIndex;
	private int totalCount;
	private HorizontalLayout slideShowLayout;

	protected abstract Component showItem(PhotogalleryViewItemTO itemTO);

	private PGService getPGService() {
		if (pgService == null)
			pgService = SpringContextHelper.getBean(PGService.class);
		return pgService;
	}

	public ImageSlideshowWindow(final String galleryDir, int index, int count) {
		this.totalCount = count;
		this.currentIndex = index;

		setResizable(false);
		center();

		addStyleName("grass-image-slideshow-window");

		slideShowLayout = new HorizontalLayout();
		slideShowLayout.setSizeFull();
		layout.addComponent(slideShowLayout);

		addAction(new ShortcutListener("Prev", KeyCode.ARROW_LEFT, null) {
			private static final long serialVersionUID = -6194478959368277077L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (currentIndex > 0) {
					changeItem(galleryDir, currentIndex - 1);
				}
			}
		});

		addAction(new ShortcutListener("Next", KeyCode.ARROW_RIGHT, null) {
			private static final long serialVersionUID = -6194478959368277077L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (currentIndex < totalCount - 1) {
					changeItem(galleryDir, currentIndex + 1);
				}
			}
		});

	}

	protected void changeItem(String galleryDir, int index) {
		currentIndex = index;
		PGService pgService = getPGService();

		try {
			PhotogalleryViewItemTO itemTO = pgService.getSlideshowItem(galleryDir, index);

			Component slideshowComponent = showItem(itemTO);

			slideShowLayout.removeAllComponents();
			slideShowLayout.addComponent(slideshowComponent);
			slideShowLayout.setComponentAlignment(slideshowComponent, Alignment.MIDDLE_CENTER);
			slideShowLayout.setExpandRatio(slideshowComponent, 1);

			setCaption((index + 1) + "/" + totalCount + " " + itemTO.getName());
		} catch (Exception e) {
			UIUtils.showWarning("Zobrazení položky se nezdařilo");
		}
	}

	@Override
	public void attach() {
		focus();
		super.attach();
	}
}

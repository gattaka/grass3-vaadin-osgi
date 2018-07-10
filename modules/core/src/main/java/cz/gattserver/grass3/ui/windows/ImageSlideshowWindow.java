package cz.gattserver.grass3.ui.windows;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ImageSlideshowWindow extends WebWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	protected int currentIndex;
	protected int totalCount;
	protected Label itemLabel;
	protected CssLayout slideShowLayout;

	public abstract void showItem(int index);

	public ImageSlideshowWindow(int count) {
		this.totalCount = count;

		setResizable(false);
		setSizeFull();

		addStyleName("grass-image-slideshow-window");

		layout.addStyleName("grass-scrollable");

		itemLabel = new Label();
		itemLabel.setStyleName("white-bold-label");
		itemLabel.setSizeUndefined();

		VerticalLayout itemLayout = new VerticalLayout();

		slideShowLayout = new CssLayout();

		itemLayout.addComponent(itemLabel);
		itemLayout.setComponentAlignment(itemLabel, Alignment.BOTTOM_CENTER);
		itemLayout.addComponent(slideShowLayout);
		itemLayout.setComponentAlignment(slideShowLayout, Alignment.TOP_CENTER);

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

	@Override
	public void attach() {
		focus();
		super.attach();
	}
}

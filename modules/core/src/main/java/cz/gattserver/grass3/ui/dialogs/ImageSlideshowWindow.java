package cz.gattserver.grass3.ui.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class ImageSlideshowWindow extends WebDialog {

	private static final long serialVersionUID = 4928404864735034779L;

	protected int currentIndex;
	protected int totalCount;
	protected Label itemLabel;
	protected Div slideShowLayout;

	public abstract void showItem(int index);

	public ImageSlideshowWindow(int count) {
		this.totalCount = count;

		setSizeFull();

		// TODO
		// addclaStyleName("grass-image-slideshow-window");

		layout.addClassName("grass-scrollable");

		itemLabel = new Label();
		itemLabel.addClassName("white-bold-label");
		itemLabel.setSizeUndefined();

		VerticalLayout itemLayout = new VerticalLayout();

		slideShowLayout = new Div();

		itemLayout.add(itemLabel);
		itemLayout.setHorizontalComponentAlignment(Alignment.CENTER, itemLabel);
		itemLayout.add(slideShowLayout);
		itemLayout.setHorizontalComponentAlignment(Alignment.CENTER, slideShowLayout);

		layout.add(itemLayout);
		layout.setHorizontalComponentAlignment(Alignment.CENTER, itemLayout);
		layout.setSizeFull();

		Shortcuts.addShortcutListener(this, () -> {
			if (currentIndex > 0)
				showItem(currentIndex - 1);
		}, Key.ARROW_LEFT);

		Shortcuts.addShortcutListener(this, () -> {
			if (currentIndex < totalCount - 1)
				showItem(currentIndex + 1);
		}, Key.ARROW_RIGHT);

	}
}

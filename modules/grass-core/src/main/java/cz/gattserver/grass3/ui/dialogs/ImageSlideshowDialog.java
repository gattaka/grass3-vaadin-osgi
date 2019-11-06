package cz.gattserver.grass3.ui.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;

public abstract class ImageSlideshowDialog extends Dialog {

	private static final long serialVersionUID = 4928404864735034779L;

	protected int currentIndex;
	protected int totalCount;
	protected Div itemLabel;
	protected Div slideShowLayout;

	public abstract void showItem(int index);

	public ImageSlideshowDialog(int count) {
		this.totalCount = count;

		Div layout = new Div();
		layout.getStyle().set("text-align", "center").set("margin", "-16px -20px -20px -20px");
		add(layout);

		itemLabel = new Div();
		itemLabel.getStyle().set("margin-bottom", "5px");
		itemLabel.setSizeUndefined();
		layout.add(itemLabel);

		slideShowLayout = new Div();
		layout.add(slideShowLayout);

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

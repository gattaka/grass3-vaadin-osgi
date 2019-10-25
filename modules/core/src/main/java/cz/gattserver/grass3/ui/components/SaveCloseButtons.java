package cz.gattserver.grass3.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import cz.gattserver.grass3.ui.components.button.CloseButton;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.grass3.ui.util.UIUtils;

public class SaveCloseButtons extends HorizontalLayout {

	private static final long serialVersionUID = 3909022460514320026L;

	public SaveCloseButtons(ComponentEventListener<ClickEvent<Button>> saveClickListener,
			ComponentEventListener<ClickEvent<Button>> closeClickListener) {
		addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		setJustifyContentMode(JustifyContentMode.BETWEEN);
		setSpacing(false);
		setSizeFull();

		add(new SaveButton(saveClickListener));
		add(new CloseButton(closeClickListener));
	}

}

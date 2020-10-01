package cz.gattserver.grass3.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

import cz.gattserver.grass3.ui.components.button.SaveButton;

public class SaveCloseLayout extends OperationsLayout {

	private static final long serialVersionUID = 3909022460514320026L;

	public SaveCloseLayout(ComponentEventListener<ClickEvent<Button>> saveClickListener,
			ComponentEventListener<ClickEvent<Button>> closeClickListener) {
		super(closeClickListener);
		setJustifyContentMode(JustifyContentMode.BETWEEN);
		setSpacing(false);
		setWidthFull();

		add(new SaveButton(saveClickListener));
	}

}

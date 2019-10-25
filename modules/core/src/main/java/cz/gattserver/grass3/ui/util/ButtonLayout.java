package cz.gattserver.grass3.ui.util;

import com.vaadin.flow.component.html.Div;

public class ButtonLayout extends Div {

	private static final long serialVersionUID = -5167038626700280420L;

	public ButtonLayout() {
		this(true);
	}

	public ButtonLayout(boolean topMargin) {
		addClassName("button-div");
		if (topMargin)
			addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
	}
}

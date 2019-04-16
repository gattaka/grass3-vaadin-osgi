package cz.gattserver.grass3.ui.windows;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class DetailWindow extends WebWindow {

	private static final long serialVersionUID = -4989848867002620787L;

	public DetailWindow(String name) {
		super(name);
	}

	protected Label addDetailLine(String caption, String content) {
		Label label;
		layout.addComponent(new Label("<strong>" + caption + "</strong>", ContentMode.HTML));
		label = new Label(content);
		layout.addComponent(label);
		return label;
	}

	protected void addDetailLine(String caption, Component content) {
		layout.addComponent(new Label("<strong>" + caption + "</strong>", ContentMode.HTML));
		layout.addComponent(content);
	}

}

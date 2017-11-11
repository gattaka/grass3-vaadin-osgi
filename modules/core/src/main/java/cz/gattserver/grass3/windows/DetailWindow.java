package cz.gattserver.grass3.windows;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.web.common.window.WebWindow;

public abstract class DetailWindow extends WebWindow {

	private static final long serialVersionUID = -4989848867002620787L;

	protected VerticalLayout layout;

	public DetailWindow(String name) {
		super(name);
		layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
	}

	protected Label addDetailLine(String caption, String content) {
		Label label;
		layout.addComponent(new Label("<strong>" + caption + "</strong>",
				ContentMode.HTML));
		layout.addComponent(label = new Label(content));
		return label;
	}

	protected void addDetailLine(String caption, Component content) {
		layout.addComponent(new Label("<strong>" + caption + "</strong>",
				ContentMode.HTML));
		layout.addComponent(content);
	}

}

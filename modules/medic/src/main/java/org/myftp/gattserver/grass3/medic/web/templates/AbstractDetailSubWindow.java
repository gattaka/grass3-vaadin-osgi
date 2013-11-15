package org.myftp.gattserver.grass3.medic.web.templates;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractDetailSubWindow extends
		AbstractTriggerComponentSubWindow {

	private static final long serialVersionUID = -4989848867002620787L;

	protected VerticalLayout layout;

	public AbstractDetailSubWindow(String name, Component triggerComponent) {
		super(name, triggerComponent);

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

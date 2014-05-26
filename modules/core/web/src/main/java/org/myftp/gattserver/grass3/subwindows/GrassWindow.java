package org.myftp.gattserver.grass3.subwindows;

import org.myftp.gattserver.grass3.SpringContextHelper;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GrassWindow extends Window {

	private static final long serialVersionUID = -9184044674542039306L;

	private VerticalLayout layout = new VerticalLayout();

	protected Component[] triggerComponents;

	public GrassWindow(String name, Component... triggerComponents) {
		super(name);
		SpringContextHelper.inject(this);

		this.triggerComponents = triggerComponents;
		for (Component component : triggerComponents)
			component.setEnabled(false);

		setContent(layout);

		layout.setSpacing(true);
		layout.setMargin(true);

		addAction(new Window.CloseShortcut(this, KeyCode.ESCAPE));

		center();

		addAttachListener(new AttachListener() {
			private static final long serialVersionUID = -2969249056636674086L;

			@Override
			public void attach(AttachEvent event) {
				focus();
			}
		});

		addCloseListener(new CloseListener() {
			private static final long serialVersionUID = 3077228408502890498L;

			@Override
			public void windowClose(CloseEvent e) {
				onClose(e);
			}
		});
	}

	protected void onClose(CloseEvent e) {
		for (Component component : triggerComponents)
			component.setEnabled(true);
	};

	protected void addComponent(Component component) {
		layout.addComponent(component);
	}
}

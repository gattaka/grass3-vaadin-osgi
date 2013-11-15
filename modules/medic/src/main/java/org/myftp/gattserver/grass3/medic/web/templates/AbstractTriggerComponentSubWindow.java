package org.myftp.gattserver.grass3.medic.web.templates;

import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.ui.Component;

public abstract class AbstractTriggerComponentSubWindow extends GrassSubWindow {

	private static final long serialVersionUID = -3667213090320225231L;

	public AbstractTriggerComponentSubWindow(String name,
			final Component triggerComponent) {
		super(name);

		triggerComponent.setEnabled(false);
		
		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				triggerComponent.setEnabled(true);
			}

		});
	}

}

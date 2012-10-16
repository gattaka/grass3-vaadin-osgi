package org.myftp.gattserver.grass3.template;

import com.vaadin.ui.Window.Notification;

public class GrassNotification extends Notification {

	private static final long serialVersionUID = -8098572674068363365L;

	public GrassNotification(String caption, int type) {
		super(caption, type);
		setDelayMsec(Notification.DELAY_FOREVER);
	}

}

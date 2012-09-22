package org.myftp.gattserver.grass3.template;

import com.vaadin.ui.Window.Notification;

public class GrassNotification extends Notification {

	private static final long serialVersionUID = -8098572674068363365L;

	public GrassNotification(String arg0, int arg1) {
		super(arg0, arg1);
		setDelayMsec(Notification.DELAY_FOREVER);
	}

}

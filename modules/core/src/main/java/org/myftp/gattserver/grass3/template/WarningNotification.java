package org.myftp.gattserver.grass3.template;

import com.vaadin.ui.Window.Notification;

public class WarningNotification extends GrassNotification {

	private static final long serialVersionUID = 7344091799441719094L;

	public WarningNotification(String caption) {
		super(caption, Notification.TYPE_WARNING_MESSAGE);
	}

}

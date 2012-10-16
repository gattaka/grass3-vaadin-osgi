package org.myftp.gattserver.grass3.template;

import com.vaadin.ui.Window.Notification;

public class ErrorNotification extends GrassNotification {

	private static final long serialVersionUID = 7344091799441719094L;

	public ErrorNotification(String caption) {
		super(caption, Notification.TYPE_ERROR_MESSAGE);
	}

}

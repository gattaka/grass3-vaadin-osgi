package cz.gattserver.grass3.ui.util;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Notification.Type;

import cz.gattserver.grass3.GrassUI;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.InfoWindow;
import cz.gattserver.web.common.window.WarnWindow;

public class UIUtils {

	private UIUtils() {
	}

	/**
	 * Získá uživatele
	 */
	public static UserInfoDTO getUser() {
		return getGrassUI().getUser();
	}

	/**
	 * Získá aktuální UI jako {@link GrassUI}
	 */
	public static GrassUI getGrassUI() {
		return (GrassUI) UI.getCurrent();
	}

	/**
	 * Přejde na stránku
	 */
	public static void redirect(String uri) {
		Page.getCurrent().setLocation(uri);
	}

	/**
	 * Notifikace pomocí {@link Notification}
	 */
	public static void showSilentInfo(String caption) {
		Notification.show(caption, Type.TRAY_NOTIFICATION);
	}

	/**
	 * Notifikace pomocí {@link InfoWindow}
	 */
	public static void showInfo(String caption) {
		InfoWindow infoSubwindow = new InfoWindow(caption);
		getGrassUI().addWindow(infoSubwindow);
	}

	/**
	 * Notifikace varování pomocí {@link WarnWindow}
	 */
	public static void showWarning(String caption) {
		WarnWindow warnSubwindow = new WarnWindow(caption);
		getGrassUI().addWindow(warnSubwindow);
	}

	/**
	 * Notifikace chyby pomocí {@link ErrorWindow}
	 */
	public static void showError(String caption) {
		ErrorWindow errorSubwindow = new ErrorWindow(caption);
		getGrassUI().addWindow(errorSubwindow);
	}

	public static void showErrorPage500() {
		throw new GrassPageException(500);
	}

	public static void showErrorPage404() {
		throw new GrassPageException(404);
	}

	public static void showErrorPage403() {
		throw new GrassPageException(403);
	}

}

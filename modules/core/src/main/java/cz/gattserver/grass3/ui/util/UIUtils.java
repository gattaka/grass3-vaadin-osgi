package cz.gattserver.grass3.ui.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.ui.GrassUI;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.InfoDialog;
import cz.gattserver.web.common.ui.window.WarnDialog;

public class UIUtils {

	private UIUtils() {
	}

	/**
	 * Získá uživatele
	 */
	public static UserInfoTO getUser() {
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
		UI.getCurrent().getPage().setLocation(uri);
	}

	/**
	 * Notifikace pomocí {@link Notification}
	 */
	public static void showSilentInfo(String caption) {
		Notification.show(caption);
	}

	/**
	 * Notifikace pomocí {@link InfoDialog}
	 */
	public static void showInfo(String caption) {
		new InfoDialog(caption).open();
	}

	/**
	 * Notifikace varování pomocí {@link WarnDialog}
	 */
	public static void showWarning(String caption) {
		new WarnDialog(caption).open();
	}

	/**
	 * Notifikace chyby pomocí {@link ErrorDialog}
	 */
	public static void showError(String caption) {
		new ErrorDialog(caption).open();
	}

}

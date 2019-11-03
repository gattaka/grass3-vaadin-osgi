package cz.gattserver.grass3.ui.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.InfoDialog;
import cz.gattserver.web.common.ui.window.WarnDialog;

public class UIUtils {

	public static final String SPACING_CSS_VAR = "var(--lumo-space-m)";
	public static final String BUTTON_SIZE_CSS_VAR = "var(--lumo-button-size)";

	public static final String TOP_MARGIN_CSS_CLASS = "top-margin";
	public static final String TOP_CLEAN_CSS_CLASS = "top-clean";
	public static final String TOP_PULL_CSS_CLASS = "top-pull";
	public static final String THUMBNAIL_200_CSS_CLASS = "thumbnail-200";

	private UIUtils() {
	}

	/**
	 * Přidá styl, aby pole bylo malé
	 */
	public static TextField asSmall(TextField textField) {
		textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		return textField;
	}

	public static <T> ComboBox<T> asSmall(ComboBox<T> comboBox) {
		comboBox.getElement().setAttribute("theme", TextFieldVariant.LUMO_SMALL.getVariantName());
		return comboBox;
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

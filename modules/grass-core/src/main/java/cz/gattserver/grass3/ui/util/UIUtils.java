package cz.gattserver.grass3.ui.util;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
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

	/**
	 * Přidá styl, aby combo bylo malé
	 */
	public static <T> ComboBox<T> asSmall(ComboBox<T> comboBox) {
		comboBox.getElement().setAttribute("theme", TextFieldVariant.LUMO_SMALL.getVariantName());
		return comboBox;
	}

	/**
	 * Přidá filtrovací pole do záhlaví gridu
	 */
	public static void addHeaderTextField(HeaderCell cell,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
		TextField field = UIUtils.asSmall(new TextField());
		field.setWidthFull();
		field.addValueChangeListener(listener);
		cell.setComponent(field);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> void addHeaderComboBox(HeaderCell cell, Class<T> enumType,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		addHeaderComboBox(cell, enumType.getEnumConstants(), itemLabelGenerator, listener);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> void addHeaderComboBox(HeaderCell cell, T[] values,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		addHeaderComboBox(cell, Arrays.asList(values), itemLabelGenerator, listener);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> void addHeaderComboBox(HeaderCell cell, Collection<T> values,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		ComboBox<T> typeColumnField = UIUtils.asSmall(new ComboBox<>(null, values));
		typeColumnField.setWidthFull();
		typeColumnField.addValueChangeListener(listener);
		typeColumnField.setItemLabelGenerator(itemLabelGenerator);
		cell.setComponent(typeColumnField);
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

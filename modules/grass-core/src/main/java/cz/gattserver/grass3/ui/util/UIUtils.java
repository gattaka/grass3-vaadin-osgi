package cz.gattserver.grass3.ui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

import cz.gattserver.grass3.ui.js.JScriptItem;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.InfoDialog;
import cz.gattserver.web.common.ui.window.WarnDialog;

public class UIUtils {

	public static final String SPACING_CSS_VAR = "var(--lumo-space-m)";
	public static final String BUTTON_SIZE_CSS_VAR = "var(--lumo-button-size)";
	public static final String FIELD_SIZE_CSS_VAR = "var(--lumo-text-field-size)";
	public static final String FIELD_CAPTION_SIZE_CSS_VAR = "1.5em";

	public static final String TOP_MARGIN_CSS_CLASS = "top-margin";
	public static final String TOP_CLEAN_CSS_CLASS = "top-clean";
	public static final String TOP_PULL_CSS_CLASS = "top-pull";
	public static final String THUMBNAIL_200_CSS_CLASS = "thumbnail-200";
	public static final String BUTTON_LINK_CSS_CLASS = "button-link";

	public static final String GRID_ICON_CSS_CLASS = "grid-icon-img";

	private UIUtils() {
	}

	/**
	 * Zjistí, zda je používáno mobilní zařízení
	 */
	public static boolean isMobileDevice() {
		WebBrowser wb = VaadinSession.getCurrent().getBrowser();
		ExtendedClientDetails ecd = UI.getCurrent().getInternals().getExtendedClientDetails();
		if (ecd != null && !ecd.isTouchDevice())
			return false;
		return wb.isIPhone() || wb.isAndroid() || wb.isWindowsPhone() || ecd != null && ecd.isIPad();
	}

	/**
	 * Scroll v gridu na pozici
	 */
	public static void scrollGridToIndex(Grid<?> grid, int index) {
//		UI.getCurrent().getPage().executeJs("$0._scrollToIndex(" + index + ")", grid.getElement());
		grid.scrollToIndex(index);
	}

	/**
	 * Ostyluje Grid tak, jak vypadají všechny tabulky v systému
	 */
	public static void applyGrassDefaultStyle(Grid<?> grid) {
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
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
	public static TextField addHeaderTextField(HeaderCell cell,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
		TextField field = UIUtils.asSmall(new TextField());
		field.setWidthFull();
		field.addValueChangeListener(listener);
		field.setValueChangeMode(ValueChangeMode.EAGER);
		cell.setComponent(field);
		return field;
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> ComboBox<T> addHeaderComboBox(HeaderCell cell, Class<T> enumType,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		return addHeaderComboBox(cell, enumType.getEnumConstants(), itemLabelGenerator, listener);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> ComboBox<T> addHeaderComboBox(HeaderCell cell, T[] values,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		return addHeaderComboBox(cell, Arrays.asList(values), itemLabelGenerator, listener);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> ComboBox<T> addHeaderComboBox(HeaderCell cell, Collection<T> values,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		ComboBox<T> combo = UIUtils.asSmall(new ComboBox<>(null, values));
		combo.setWidthFull();
		combo.setRequired(false);
		combo.setClearButtonVisible(true);
		combo.addValueChangeListener(listener);
		combo.setItemLabelGenerator(itemLabelGenerator);
		cell.setComponent(combo);
		return combo;
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

	/**
	 * Nahraje více JS skriptů, synchronně za sebou (mohou se tedy navzájem na
	 * sebe odkazovat a bude zaručeno, že 1. skript bude celý nahrán před 2.
	 * skriptem, který využívá jeho funkcí)
	 * 
	 * @param scripts
	 *            skripty, které budou nahrány
	 */
	public static PendingJavaScriptResult loadJS(JScriptItem... scripts) {
		return loadJS(Arrays.asList(scripts));
	}

	/**
	 * Nahraje více JS skriptů, synchronně za sebou (mohou se tedy navzájem na
	 * sebe odkazovat a bude zaručeno, že 1. skript bude celý nahrán před 2.
	 * skriptem, který využívá jeho funkcí)
	 * 
	 * @param scripts
	 *            skripty, které budou nahrány
	 */
	public static PendingJavaScriptResult loadJS(List<JScriptItem> scripts) {
		StringBuilder builder = new StringBuilder();
		buildJSBatch(builder, 0, scripts);
		return UI.getCurrent().getPage().executeJs(builder.toString());
	}

	private static void buildJSBatch(StringBuilder builder, int index, List<JScriptItem> scripts) {
		if (index >= scripts.size())
			return;

		JScriptItem js = scripts.get(index);
		String chunk = js.getScript();
		// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
		// obejít problém se závislosí pluginů na úložišti theme apod. a
		// přitom umožnit aby se JS odkazovali na externí zdroje
		if (!js.isPlain()) {
			if (!chunk.toLowerCase().startsWith("http://") && !chunk.toLowerCase().startsWith("https://")) {
				chunk = "\"" + getContextPath() + "/frontend/" + chunk + "\"";
			} else {
				chunk = "\"" + chunk + "\"";
			}
			builder.append("$.getScript(").append(chunk).append(", function(){");
			buildJSBatch(builder, index + 1, scripts);
			builder.append("});");
		} else {
			builder.append(chunk);
			buildJSBatch(builder, index + 1, scripts);
		}
	}

	/**
	 * Nahraje CSS
	 * 
	 * @param link
	 *            odkaz k css souboru - relativní, absolutní (http://...)
	 */
	public static void loadCSS(String link) {
		StringBuilder loadStylesheet = new StringBuilder();
		loadStylesheet.append("var head=document.getElementsByTagName('head')[0];")
				.append("var link=document.createElement('link');").append("link.type='text/css';")
				.append("link.rel='stylesheet';").append("link.href='" + link + "';").append("head.appendChild(link);");
		UI.getCurrent().getPage().executeJs(loadStylesheet.toString());
	}

	public static String getContextPath() {
		return VaadinRequest.getCurrent().getContextPath();
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix dle pageFactory
	 */
	public static String getPageURL(PageFactory pageFactory) {
		return getContextPath() + "/" + pageFactory.getPageName();
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix
	 */
	public static String getPageURL(String suffix) {
		return getContextPath() + "/" + suffix;
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix dle pageFactory + relativní URL
	 */
	public static String getPageURL(PageFactory pageFactory, String... relativeURLs) {
		if (relativeURLs.length == 1) {
			return getPageURL(pageFactory) + "/" + relativeURLs[0];
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append(getPageURL(pageFactory));
			for (String relativeURL : relativeURLs) {
				if (relativeURL != null) {
					buffer.append("/");
					buffer.append(relativeURL);
				}
			}
			return buffer.toString();
		}
	}

}

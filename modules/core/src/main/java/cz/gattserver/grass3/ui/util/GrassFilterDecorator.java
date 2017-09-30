package cz.gattserver.grass3.ui.util;

import java.util.Locale;

import com.vaadin.server.Resource;
import com.vaadin.v7.shared.ui.datefield.Resolution;

public class GrassFilterDecorator {
	private static final long serialVersionUID = 234978256739631357L;

	public boolean usePopupForNumericProperty(Object propertyId) {
		return false;
	}

	public boolean isTextFilterImmediate(Object propertyId) {
		return true;
	}

	public String getToCaption() {
		return "Do";
	}

	public int getTextChangeTimeout(Object propertyId) {
		return 0;
	}

	public String getSetCaption() {
		return "Filtrovat";
	}

	public Locale getLocale() {
		return new Locale("cs");
	}

	public String getFromCaption() {
		return "Od";
	}

	public Resource getEnumFilterIcon(Object propertyId, Object value) {
		return null;
	}

	public String getEnumFilterDisplayName(Object propertyId, Object value) {
		return null;
	}

	public String getDateFormatPattern(Object propertyId) {
		return "d.M.yyyy";
	}

	public Resolution getDateFieldResolution(Object propertyId) {
		return Resolution.DAY;
	}

	public String getClearCaption() {
		return "Vymazat";
	}

	public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
		return null;
	}

	public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
		return null;
	}

	public String getAllItemsVisibleString() {
		return null;
	}

	public String getNumberValidationErrorMessage() {
		return null;
	}

}

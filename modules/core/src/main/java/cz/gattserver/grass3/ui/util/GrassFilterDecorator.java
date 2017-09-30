package cz.gattserver.grass3.ui.util;

import java.util.Locale;

import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.server.Resource;
import com.vaadin.v7.shared.ui.datefield.Resolution;

public class GrassFilterDecorator implements FilterDecorator {
	private static final long serialVersionUID = 234978256739631357L;

	@Override
	public boolean usePopupForNumericProperty(Object propertyId) {
		return false;
	}

	@Override
	public boolean isTextFilterImmediate(Object propertyId) {
		return true;
	}

	@Override
	public String getToCaption() {
		return "Do";
	}

	@Override
	public int getTextChangeTimeout(Object propertyId) {
		return 0;
	}

	@Override
	public String getSetCaption() {
		return "Filtrovat";
	}

	@Override
	public NumberFilterPopupConfig getNumberFilterPopupConfig() {
		return null;
	}

	@Override
	public Locale getLocale() {
		return new Locale("cs");
	}

	@Override
	public String getFromCaption() {
		return "Od";
	}

	@Override
	public Resource getEnumFilterIcon(Object propertyId, Object value) {
		return null;
	}

	@Override
	public String getEnumFilterDisplayName(Object propertyId, Object value) {
		return null;
	}

	@Override
	public String getDateFormatPattern(Object propertyId) {
		return "d.M.yyyy";
	}

	@Override
	public Resolution getDateFieldResolution(Object propertyId) {
		return Resolution.DAY;
	}

	@Override
	public String getClearCaption() {
		return "Vymazat";
	}

	@Override
	public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
		return null;
	}

	@Override
	public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
		return null;
	}

	@Override
	public String getAllItemsVisibleString() {
		return null;
	}

	@Override
	public String getNumberValidationErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}

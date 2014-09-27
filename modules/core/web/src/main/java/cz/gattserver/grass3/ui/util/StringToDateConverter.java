package cz.gattserver.grass3.ui.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StringToDateConverter extends
		com.vaadin.data.util.converter.StringToDateConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	private String dateFormat;

	public StringToDateConverter() {
		dateFormat = "d.MM.yyyy";
	}

	public StringToDateConverter(String format) {
		dateFormat = format;
	}

	@Override
	protected DateFormat getFormat(Locale locale) {
		return new SimpleDateFormat(dateFormat);
	}

	public DateFormat getFormat() {
		return getFormat(null);
	}

}

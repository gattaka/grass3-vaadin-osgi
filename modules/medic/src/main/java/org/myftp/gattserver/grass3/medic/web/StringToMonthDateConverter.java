package org.myftp.gattserver.grass3.medic.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToDateConverter;

public class StringToMonthDateConverter extends StringToDateConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy - MMMMM");

	private static StringToMonthDateConverter INSTANCE;

	public static StringToMonthDateConverter getInstance() {
		if (INSTANCE == null)
			INSTANCE = new StringToMonthDateConverter();
		return INSTANCE;
	}

	private StringToMonthDateConverter() {
	};

	@Override
	protected DateFormat getFormat(Locale locale) {
		return dateFormat;
	}

	public static String format(Date date) {
		return getInstance().getFormat(null).format(date);
	}
}

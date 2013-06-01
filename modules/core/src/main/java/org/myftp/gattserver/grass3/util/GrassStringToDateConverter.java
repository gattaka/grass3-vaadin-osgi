package org.myftp.gattserver.grass3.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToDateConverter;

public class GrassStringToDateConverter extends StringToDateConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	private static GrassStringToDateConverter INSTANCE;

	public static GrassStringToDateConverter getInstance() {
		if (INSTANCE == null)
			INSTANCE = new GrassStringToDateConverter();
		return INSTANCE;
	}

	private GrassStringToDateConverter() {
	};

	@Override
	protected DateFormat getFormat(Locale locale) {
		return dateFormat;
	}

	public static String format(Date date) {
		return getInstance().getFormat(null).format(date);
	}
}

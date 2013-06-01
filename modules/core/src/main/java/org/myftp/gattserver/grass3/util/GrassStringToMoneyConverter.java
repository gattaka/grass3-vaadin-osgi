package org.myftp.gattserver.grass3.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToNumberConverter;

public class GrassStringToMoneyConverter extends StringToNumberConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	private NumberFormat priceFormat = NumberFormat
			.getCurrencyInstance(new Locale("cs", "CZ"));

	private static GrassStringToMoneyConverter INSTANCE;

	public static GrassStringToMoneyConverter getInstance() {
		if (INSTANCE == null)
			INSTANCE = new GrassStringToMoneyConverter();
		return INSTANCE;
	}

	private GrassStringToMoneyConverter() {
	};

	@Override
	protected NumberFormat getFormat(Locale locale) {
		return priceFormat;
	}

	public static String format(Integer price) {
		return getInstance().getFormat(null).format(price);
	}

}

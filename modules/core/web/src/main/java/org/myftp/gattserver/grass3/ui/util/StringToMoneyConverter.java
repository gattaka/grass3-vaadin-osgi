package org.myftp.gattserver.grass3.ui.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToNumberConverter;

public class StringToMoneyConverter extends StringToNumberConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	private NumberFormat priceFormat;

	public StringToMoneyConverter() {
		priceFormat = NumberFormat.getCurrencyInstance(new Locale("cs", "CZ"));
		((DecimalFormat) priceFormat).setMaximumFractionDigits(2);
		((DecimalFormat) priceFormat).setMinimumFractionDigits(2);
	}

	public String format(int price) {
		return priceFormat.format(price);
	}

	public String format(double price) {
		return priceFormat.format(price);
	}

	@Override
	protected NumberFormat getFormat(Locale locale) {
		return priceFormat;
	}

}

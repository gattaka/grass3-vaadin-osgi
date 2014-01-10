package org.myftp.gattserver.grass3.ui.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class StringToFixedSizeDoubleConverter extends com.vaadin.data.util.converter.StringToDoubleConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	@Override
	protected NumberFormat getFormat(Locale locale) {
		return new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("cs")));
	}

}

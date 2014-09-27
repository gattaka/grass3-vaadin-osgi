package cz.gattserver.grass3.ui.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class StringToFixedSizeDoubleConverter extends com.vaadin.data.util.converter.StringToDoubleConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	private NumberFormat format;

	public StringToFixedSizeDoubleConverter() {
		this(2, 2);
	}

	public StringToFixedSizeDoubleConverter(int minFractionDigits, int maxFractionDigits) {
		format = new DecimalFormat("#,##0", new DecimalFormatSymbols(new Locale("cs")));
		((DecimalFormat) format).setMaximumFractionDigits(maxFractionDigits);
		((DecimalFormat) format).setMinimumFractionDigits(minFractionDigits);
	}

	public String format(int number) {
		return format.format(number);
	}

	public String format(double number) {
		return format.format(number);
	}

	@Override
	protected NumberFormat getFormat(Locale locale) {
		return format;
	}

}

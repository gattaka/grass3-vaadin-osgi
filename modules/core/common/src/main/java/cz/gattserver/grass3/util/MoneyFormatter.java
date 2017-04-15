package cz.gattserver.grass3.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormatter {

	private static NumberFormat priceFormat = NumberFormat.getCurrencyInstance(new Locale("cs", "CZ"));

	public static String format(BigDecimal price) {
		return priceFormat.format(price.doubleValue());
	}

	public static String format(double price) {
		return priceFormat.format(price);
	}

}

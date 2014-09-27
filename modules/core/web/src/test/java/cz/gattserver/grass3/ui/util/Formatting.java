package cz.gattserver.grass3.ui.util;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.ui.util.StringToFixedSizeDoubleConverter;
import cz.gattserver.grass3.ui.util.StringToMoneyConverter;

public class Formatting {

	@Test
	public void testMoney() {
		StringToMoneyConverter converter = new StringToMoneyConverter();

		assertEquals("200,00 Kč", converter.format(200.0));
		assertEquals("200,00 Kč", converter.format(200));

		assertEquals("200,00 Kč", converter.format(200.005));
		assertEquals("200,90 Kč", converter.format(200.9));
	}

	@Test
	public void testFixed() {
		StringToFixedSizeDoubleConverter converter = new StringToFixedSizeDoubleConverter(0, 3);

		assertEquals("200", converter.format(200.0));
		assertEquals("200", converter.format(200));

		assertEquals("200,005", converter.format(200.005));
		assertEquals("200,9", converter.format(200.9));
	}
}

package cz.gattserver.grass3.ui.util;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.util.MoneyFormatter;

public class Formatting {

	@Test
	public void testMoney() {
		assertEquals("200,00 Kč", MoneyFormatter.format(200.0));
		assertEquals("200,00 Kč", MoneyFormatter.format(200));

		assertEquals("200,00 Kč", MoneyFormatter.format(200.005));
		assertEquals("200,90 Kč", MoneyFormatter.format(200.9));
	}

}

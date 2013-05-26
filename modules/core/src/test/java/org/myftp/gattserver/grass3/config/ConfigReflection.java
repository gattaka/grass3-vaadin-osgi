package org.myftp.gattserver.grass3.config;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class ConfigReflection {

	@Test
	public void test() throws Exception {
		Integer i = AbstractConfiguration.itemValueOf("42", Integer.class);
		assertTrue(i == 42);

		Double d = AbstractConfiguration.itemValueOf("42", Double.class);
		assertTrue(d == 42);
	}

	@Test
	public void testException() throws Exception {
		try {
			AbstractConfiguration.itemValueOf("20.5.2013", Date.class);
			fail("nevyhozena výjimka");
		} catch (NoSuchMethodException e) {
			// ok
		}
	}

}

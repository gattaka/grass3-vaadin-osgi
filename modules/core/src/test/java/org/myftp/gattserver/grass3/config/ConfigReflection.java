package org.myftp.gattserver.grass3.config;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class ConfigReflection {

	@Test
	public void test() throws Exception {
		Integer integer = StringDeserializer.deserialize("42", Integer.class);
		assertTrue(integer == 42);

		Double doub = StringDeserializer.deserialize("42", Double.class);
		assertTrue(doub == 42);

		Date date = StringDeserializer.deserialize("20.5.2013", Date.class);
		Calendar cal = Calendar.getInstance();
		cal.set(2013, 5, 20);
		assertTrue(cal.getTime().equals(date));
	}

	@Test
	public void testException() throws Exception {
		try {
			StringDeserializer.deserialize("20.5.2013", Date.class);
			fail("nevyhozena výjimka");
		} catch (Exception e) {
			// ok
		}
	}

}

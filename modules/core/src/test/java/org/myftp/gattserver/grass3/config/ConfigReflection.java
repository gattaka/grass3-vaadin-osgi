package org.myftp.gattserver.grass3.config;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ConfigReflection {

	@Test
	public void test() throws Exception {

		String serialized;

		int i = 42;
		serialized = StringSerializer.serialize(i);
		assertTrue(StringSerializer.deserialize(serialized,
				Integer.class).equals(i));

		Double doub = 42.225;
		serialized = StringSerializer.serialize(doub);
		assertTrue(StringSerializer.deserialize(serialized,
				Double.class).equals(doub));

		Date date = Calendar.getInstance().getTime();
		serialized = StringSerializer.serialize(date);
		Date parsedDate = StringSerializer.deserialize(serialized,
				Date.class);
		assertTrue(parsedDate.equals(date));

		HashSet<String> set = new HashSet<String>();
		set.add("AAAA");
		set.add("BBbbe");
		set.add("ccCCcC");
		serialized = StringSerializer.serialize(set);
		Set<String> parsedSet = StringSerializer.deserialize(
				serialized, Set.class);
		assertTrue(parsedSet.equals(set));

	}

}

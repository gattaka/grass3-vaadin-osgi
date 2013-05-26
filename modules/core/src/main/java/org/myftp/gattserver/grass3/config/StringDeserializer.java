package org.myftp.gattserver.grass3.config;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

public class StringDeserializer {

	public static String serialize(Object value, Class<?> type) {
		return String.valueOf(value);
	}

	public static <T> T deserialize(String value, Class<T> type)
			throws Exception {

//		if (type == Set.class) {
//			String result = value.substring(1, value.length() - 1);
//			Set<String> resultSet = new HashSet<String>(); 
//			for (String str : result.sp)
//		}

		return (T) DatatypeConverter.parseAnySimpleType(value);
	}
}

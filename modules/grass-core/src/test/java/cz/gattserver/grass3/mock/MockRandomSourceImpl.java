package cz.gattserver.grass3.mock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.RandomSourceService;

@Primary
@Component
public class MockRandomSourceImpl implements RandomSourceService {

	public static long[] longValues;
	public static int[] intValues;

	public static int longValuesIndex = 0;
	public static int intValuesIndex = 0;

	@Override
	public long getRandomLong(long range) {
		long val = longValues[longValuesIndex];
		longValuesIndex = ++longValuesIndex % longValues.length;
		return val;
	}

	@Override
	public int getRandomInt(int range) {
		int val = intValues[intValuesIndex];
		intValuesIndex = ++intValuesIndex % intValues.length;
		return val;
	}

}

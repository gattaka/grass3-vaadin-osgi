package cz.gattserver.grass3.mock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.facades.RandomSource;

@Primary
@Component
public class MockRandomSourceImpl implements RandomSource {

	public static long nextValue = 1L;

	@Override
	public long getRandomNumber(long range) {
		return nextValue;
	}

}

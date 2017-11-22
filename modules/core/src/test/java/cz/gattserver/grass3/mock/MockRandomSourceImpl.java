package cz.gattserver.grass3.mock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.RandomSourceService;

@Primary
@Component
public class MockRandomSourceImpl implements RandomSourceService {

	public static long nextValue = 0L;

	@Override
	public long getRandomNumber(long range) {
		return nextValue;
	}

}

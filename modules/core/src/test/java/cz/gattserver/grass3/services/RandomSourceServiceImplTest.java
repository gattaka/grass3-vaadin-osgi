package cz.gattserver.grass3.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.services.RandomSourceService;
import cz.gattserver.grass3.test.GrassFacadeTest;

public class RandomSourceServiceImplTest extends GrassFacadeTest {

	@Autowired
	@Qualifier("randomSourceImpl")
	private RandomSourceService randomSource;

	@Test
	public void test() {
		assertEquals(0, randomSource.getRandomNumber(0));
		assertEquals(0, randomSource.getRandomNumber(1));
		assertTrue(randomSource.getRandomNumber(2) < 2);
	}

}

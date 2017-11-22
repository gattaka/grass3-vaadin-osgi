package cz.gattserver.grass3.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.services.RandomSourceService;
import cz.gattserver.grass3.test.AbstractTest;

public class RandomSourceServiceImplTest extends AbstractTest {

	@Autowired
	@Qualifier("randomSourceServiceImpl")
	private RandomSourceService randomSourceService;

	@Test
	public void test() {
		assertEquals(0, randomSourceService.getRandomNumber(0));
		assertEquals(0, randomSourceService.getRandomNumber(1));
		assertTrue(randomSourceService.getRandomNumber(2) < 2);
	}

}

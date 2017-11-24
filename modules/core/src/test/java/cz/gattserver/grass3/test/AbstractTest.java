package cz.gattserver.grass3.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import cz.gattserver.grass3.mock.CoreMockService;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml", "classpath:spring/security-context.xml" })
public abstract class AbstractTest {

	@Autowired
	protected CoreMockService coreMockService;
}

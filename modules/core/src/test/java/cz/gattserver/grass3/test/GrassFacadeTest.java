package cz.gattserver.grass3.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml", "classpath:spring/security-context.xml" })
public class GrassFacadeTest {

}

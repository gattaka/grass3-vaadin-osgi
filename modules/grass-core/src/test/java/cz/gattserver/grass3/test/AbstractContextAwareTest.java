package cz.gattserver.grass3.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cz.gattserver.grass3.mock.CoreMockService;
import cz.gattserver.grass3.spring.config.AppConfig;

// Tohle je tu potřeba, protože jinak si prostě spring context začne při startu stěžovat, že nemá Servlet... 
// https://quick-geek.github.io/answers/891653/index.html
@WebAppConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
public abstract class AbstractContextAwareTest {

	@Autowired
	protected CoreMockService coreMockService;
}

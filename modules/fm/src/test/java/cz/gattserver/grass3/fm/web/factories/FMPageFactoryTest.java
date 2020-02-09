package cz.gattserver.grass3.fm.web.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.test.AbstractContextAwareTest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public class FMPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("fmPageFactory")
	private PageFactory pageFactory;

	@Test
	public void testFMPageFactory() {
		assertTrue(pageFactory instanceof FMPageFactory);
		FMPageFactory factory = (FMPageFactory) pageFactory;

		assertEquals("fm", factory.getPageName());
	}

}

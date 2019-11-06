package cz.gattserver.grass3.pg.ui.pages.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.test.AbstractContextAwareTest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public class PGEditorPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("pgEditorPageFactory")
	private PageFactory pageFactory;

	@Test
	public void testPGEditorPageFactory() {
		assertTrue(pageFactory instanceof PGEditorPageFactory);
		PGEditorPageFactory factory = (PGEditorPageFactory) pageFactory;

		assertEquals("pg-editor", factory.getPageName());
	}

}

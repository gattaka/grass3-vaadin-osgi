package cz.gattserver.grass3.pg.ui.pages.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.test.AbstractContextAwareTest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public class PGViewerPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("pgViewerPageFactory")
	private PageFactory pageFactory;

	@Test
	public void testPGEditorPageFactory() {
		assertTrue(pageFactory instanceof PGViewerPageFactory);
		PGViewerPageFactory factory = (PGViewerPageFactory) pageFactory;

		assertEquals("photogallery", factory.getPageName());
	}

}

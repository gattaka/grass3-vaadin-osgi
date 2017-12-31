package cz.gattserver.grass3.pg.ui.pages.factories;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.pg.test.MockSecurityService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.test.AbstractContextAwareTest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public class PGViewerPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("pgViewerPageFactory")
	private PageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testPGEditorPageFactory() {
		assertTrue(pageFactory instanceof PGViewerPageFactory);
		PGViewerPageFactory factory = (PGViewerPageFactory) pageFactory;

		assertEquals("photogallery", factory.getPageName());

		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.ADMIN)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.AUTHOR)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.USER)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.FRIEND)));
		assertTrue(factory.isAuthorized());
	}

}

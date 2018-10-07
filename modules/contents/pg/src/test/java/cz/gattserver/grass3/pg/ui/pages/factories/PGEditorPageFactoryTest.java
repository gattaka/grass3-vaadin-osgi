package cz.gattserver.grass3.pg.ui.pages.factories;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.pg.test.MockSecurityService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.test.AbstractContextAwareTest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public class PGEditorPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("pgEditorPageFactory")
	private PageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testPGEditorPageFactory() {
		assertTrue(pageFactory instanceof PGEditorPageFactory);
		PGEditorPageFactory factory = (PGEditorPageFactory) pageFactory;

		assertEquals("pg-editor", factory.getPageName());

		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.USER)));
		assertFalse(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND)));
		assertFalse(factory.isAuthorized());
	}

}

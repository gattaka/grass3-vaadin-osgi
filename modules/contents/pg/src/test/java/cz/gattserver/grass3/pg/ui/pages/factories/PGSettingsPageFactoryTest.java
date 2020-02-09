package cz.gattserver.grass3.pg.ui.pages.factories;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.pg.test.MockSecurityService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class PGSettingsPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	private PGSettingsPageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testPGSettingsPageFactory() {
		assertTrue(pageFactory instanceof PGSettingsPageFactory);
		PGSettingsPageFactory factory = (PGSettingsPageFactory) pageFactory;

		assertEquals("Fotogalerie", factory.getSettingsCaption());
		assertEquals("photogallery", factory.getSettingsURL());

		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR)));
		assertFalse(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.USER)));
		assertFalse(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND)));
		assertFalse(factory.isAuthorized());
	}

}

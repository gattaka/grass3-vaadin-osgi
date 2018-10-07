package cz.gattserver.grass3.fm.web.factories;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.fm.test.MockSecurityService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class PGSettingsPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	private FMSettingsPageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testFMSettingsPageFactory() {
		assertTrue(pageFactory instanceof FMSettingsPageFactory);
		FMSettingsPageFactory factory = (FMSettingsPageFactory) pageFactory;

		assertEquals("Soubory", factory.getSettingsCaption());
		assertEquals("fm", factory.getSettingsURL());

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

package cz.gattserver.grass3.fm.web.factories;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.fm.test.MockSecurityService;
import cz.gattserver.grass3.fm.web.factories.FMPageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.test.AbstractContextAwareTest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public class FMPageFactoryTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("fmPageFactory")
	private PageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testFMPageFactory() {
		assertTrue(pageFactory instanceof FMPageFactory);
		FMPageFactory factory = (FMPageFactory) pageFactory;

		assertEquals("fm", factory.getPageName());

		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.ADMIN)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.FRIEND)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.USER)));
		assertFalse(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(Role.AUTHOR)));
		assertFalse(factory.isAuthorized());
	}

}

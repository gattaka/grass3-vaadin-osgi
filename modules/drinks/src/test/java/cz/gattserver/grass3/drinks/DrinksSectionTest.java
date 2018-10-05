package cz.gattserver.grass3.drinks;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.drinks.ui.pages.factories.DrinksPageFactory;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class DrinksSectionTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("drinksSection")
	private SectionService sectionService;

	@Test
	public void testFMSection() {
		assertTrue(sectionService instanceof DrinksSection);
		assertEquals("Nápoje", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof DrinksPageFactory);
		assertTrue(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.FRIEND))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.USER))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.AUTHOR))));
	}

}

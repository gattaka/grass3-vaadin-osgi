package cz.gattserver.grass3.campgames;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.campgames.ui.pages.factories.CampgamesPageFactory;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class CampgamesSectionTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("campgamesSection")
	private SectionService sectionService;

	@Test
	public void testFMSection() {
		assertTrue(sectionService instanceof CampgamesSection);
		assertEquals("Hry", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof CampgamesPageFactory);
		assertTrue(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.USER))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR))));
	}

}

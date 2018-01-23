package cz.gattserver.grass3.hw;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.hw.ui.pages.factories.HWPageFactory;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class HWSectionTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("hwSection")
	private SectionService sectionService;

	@Test
	public void testFMSection() {
		assertTrue(sectionService instanceof HWSection);
		assertEquals("HW", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof HWPageFactory);
		assertFalse(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.ADMIN))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.FRIEND))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.USER))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(Role.AUTHOR))));
	}

}

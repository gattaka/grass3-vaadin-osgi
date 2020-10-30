package cz.gattserver.grass3.hw;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.hw.ui.pages.factories.HWPageFactory;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class HWSectionTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("hwSection")
	private SectionService sectionService;

	@Test
	public void testHWSection() {
		assertTrue(sectionService instanceof HWSection);
		assertEquals("HW", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof HWPageFactory);
		assertTrue(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.USER))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR))));
	}

}

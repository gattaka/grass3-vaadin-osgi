package cz.gattserver.grass3.fm;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cz.gattserver.grass3.fm.web.factories.FMPageFactory;
import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class FMSectionTest extends AbstractContextAwareTest {

	@Autowired
	@Qualifier("fmSection")
	private SectionService sectionService;

	@Test
	public void testFMSection() {
		assertTrue(sectionService instanceof FMSection);
		assertEquals("Soubory", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof FMPageFactory);
		assertFalse(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.USER))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR))));
	}

}

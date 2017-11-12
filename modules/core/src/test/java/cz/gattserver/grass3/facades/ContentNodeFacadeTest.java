package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.test.GrassFacadeTest;

public class ContentNodeFacadeTest extends GrassFacadeTest {

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Autowired
	private UserFacade userFacade;

	@BeforeClass
	public void prepare() {
		userFacade.registrateNewUser("testuser@email.cz", "TestUser", "testUser00012xxx$");
		userFacade.registrateNewUser("testuser@email.cz", "TestUser", null);
	}

	@Test
	public void testSaveContent() {
		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");
		tags.add("testy");
		tags.add("ŘěŇšb test diakritiky");
		// contentNodeFacade.save(15L,2L , "Test obsah", tags, true, nodeId,
		// author, draft, date, draftSourceId)
		assertTrue(true);
	}

}

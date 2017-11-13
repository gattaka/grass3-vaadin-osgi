package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentTagDTO;
import cz.gattserver.grass3.test.GrassFacadeTest;

@DatabaseTearDown(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ContentNodeFacadeTest extends GrassFacadeTest {

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Test
	public void testSaveContent() {
		Long userId = mockService.createMockUser();
		mockService.createMockRootNode(); // pro posuv id
		Long nodeId = mockService.createMockRootNode();

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");
		tags.add("testy");
		tags.add("ŘěŇšb test diakritiky");

		String moduleId = "mockModule";
		Long contentId = 2L;
		String name = "Test obsah";
		Long contentNodeId = contentNodeFacade.save(moduleId, contentId, name, tags, true, nodeId, userId, false,
				LocalDateTime.now(), null);

		ContentNodeDTO contentNode = contentNodeFacade.getByID(contentNodeId);
		assertNotNull(contentNode);
		assertEquals(new Long(2L), contentNode.getContentID());
		assertEquals(moduleId, contentNode.getContentReaderID());
		for (ContentTagDTO t : contentNode.getContentTags())
			tags.remove(t.getName());
		assertTrue(tags.isEmpty());
		assertEquals(name, contentNode.getName());
		assertEquals(userId, contentNode.getAuthor().getId());
		assertEquals(nodeId, contentNode.getParent().getId());
	}

}

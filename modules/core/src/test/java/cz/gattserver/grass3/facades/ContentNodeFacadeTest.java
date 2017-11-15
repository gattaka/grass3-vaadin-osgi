package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;
import cz.gattserver.grass3.test.GrassFacadeTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ContentNodeFacadeTest extends GrassFacadeTest {

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Test
	public void testSave() {
		Long userId = mockService.createMockUser(1);
		mockService.createMockRootNode(1); // pro posuv id
		Long nodeId2 = mockService.createMockRootNode(2);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");
		tags.add("testy");
		tags.add("ŘěŇšb test diakritiky");

		String moduleId = "mockModule";
		Long contentId = 2L;
		String name = "Test obsah";
		Long contentNodeId = contentNodeFacade.save(moduleId, contentId, name, tags, true, nodeId2, userId, false,
				LocalDateTime.now(), null);

		ContentNodeDTO contentNodeByID = contentNodeFacade.getByID(contentNodeId);
		assertNotNull(contentNodeByID);
		assertEquals(moduleId, contentNodeByID.getContentReaderID());
		assertEquals(contentId, contentNodeByID.getContentID());
		for (ContentTagOverviewDTO t : contentNodeByID.getContentTags())
			tags.remove(t.getName());
		assertTrue(tags.isEmpty());
		assertEquals(name, contentNodeByID.getName());
		assertEquals(userId, contentNodeByID.getAuthor().getId());
		assertEquals(nodeId2, contentNodeByID.getParent().getId());
	}

	@Test
	public void testGetByNode() {
		Long userId1 = mockService.createMockUser(1);
		Long userId2 = mockService.createMockUser(2);
		Long nodeId1 = mockService.createMockRootNode(1);
		Long nodeId2 = mockService.createMockRootNode(2);

		Set<String> tags = new HashSet<>();

		mockService.createMockContentNode(20L, tags, nodeId1, userId1, 1);
		Long contentNode2 = mockService.createMockContentNode(30L, tags, nodeId2, userId1, 2);
		Long contentNode3 = mockService.createMockContentNode(25L, tags, nodeId2, userId2, 3);

		List<ContentNodeOverviewDTO> contentNodesByNode = contentNodeFacade.getByNode(nodeId2, 0, 10);
		assertEquals(2, contentNodesByNode.size());

		ContentNodeOverviewDTO contentNodeByNode = contentNodesByNode.get(0);
		assertEquals(contentNode3, contentNodeByNode.getId());
		assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 3, contentNodeByNode.getContentReaderID());
		assertEquals(new Long(25), contentNodeByNode.getContentID());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 3, contentNodeByNode.getName());
		assertEquals(userId2, contentNodeByNode.getAuthor().getId());
		assertEquals(nodeId2, contentNodeByNode.getParent().getId());

		contentNodeByNode = contentNodesByNode.get(1);
		assertEquals(contentNode2, contentNodeByNode.getId());
		assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 2, contentNodeByNode.getContentReaderID());
		assertEquals(new Long(30L), contentNodeByNode.getContentID());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 2, contentNodeByNode.getName());
		assertEquals(userId1, contentNodeByNode.getAuthor().getId());
		assertEquals(nodeId2, contentNodeByNode.getParent().getId());
	}

	@Test
	public void testGetByTag() {
		Long userId1 = mockService.createMockUser(1);
		Long userId2 = mockService.createMockUser(2);
		Long nodeId1 = mockService.createMockRootNode(1);
		Long nodeId2 = mockService.createMockRootNode(2);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");

		mockService.createMockContentNode(20L, tags, nodeId1, userId1, 1);
		mockService.createMockContentNode(30L, tags, nodeId2, userId1, 2);

		tags.add("něco");

		Long contentNode3 = mockService.createMockContentNode(25L, tags, nodeId2, userId2, 3);

		ContentTagOverviewDTO tag = contentTagFacade.getContentTagByName("něco");
		assertNotNull(tag);
		assertEquals("něco", tag.getName());
		assertEquals(1, tag.getContentNodesCount());

		List<ContentNodeOverviewDTO> contentNodesByTag = contentNodeFacade.getByTag(tag.getId(), 0, 10);
		assertEquals(1, contentNodesByTag.size());
		ContentNodeOverviewDTO contentNodeByTag = contentNodesByTag.get(0);
		assertEquals(contentNode3, contentNodeByTag.getId());
		assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 3, contentNodeByTag.getContentReaderID());
		assertEquals(new Long(25L), contentNodeByTag.getContentID());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 3, contentNodeByTag.getName());
		assertEquals(userId2, contentNodeByTag.getAuthor().getId());
		assertEquals(nodeId2, contentNodeByTag.getParent().getId());

		tag = contentTagFacade.getContentTagByName("pokusy");
		assertNotNull(tag);
		assertEquals("pokusy", tag.getName());
		assertEquals(3, tag.getContentNodesCount());

	}

}

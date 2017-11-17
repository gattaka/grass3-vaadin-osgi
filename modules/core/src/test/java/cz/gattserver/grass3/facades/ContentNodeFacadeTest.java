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

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.test.GrassFacadeTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ContentNodeFacadeTest extends GrassFacadeTest {

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private UserFacade userFacade;

	@Test
	public void testGetRecentAdded() {
		assertEquals(0, contentNodeFacade.getCount());

		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		Long contentNode2 = mockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

		List<ContentNodeOverviewTO> added = contentNodeFacade.getRecentAdded(0, 10);
		assertEquals(2, added.size());

		ContentNodeOverviewTO added2 = added.get(0);
		assertEquals(contentNode2, added2.getId());

		ContentNodeOverviewTO added1 = added.get(1);
		assertEquals(contentNode1, added1.getId());
	}

	@Test
	public void testGetRecentModified() {
		assertEquals(0, contentNodeFacade.getCount());

		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		Long contentNode2 = mockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

		contentNodeFacade.modify(contentNode1, "newName", true);

		List<ContentNodeOverviewTO> added = contentNodeFacade.getRecentModified(0, 10);
		assertEquals(2, added.size());

		ContentNodeOverviewTO added1 = added.get(0);
		assertEquals(contentNode1, added1.getId());

		ContentNodeOverviewTO added2 = added.get(1);
		assertEquals(contentNode2, added2.getId());
	}

	@Test
	public void testGetUserFavourite() {
		assertEquals(0, contentNodeFacade.getCount());

		Long userId1 = mockService.createMockUser(1);
		Long userId2 = mockService.createMockUser(2);
		Long nodeId1 = mockService.createMockRootNode(1);
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		mockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

		userFacade.addContentToFavourites(contentNode1, userId1);

		assertEquals(1, contentNodeFacade.getUserFavouriteCount(userId1));
		assertEquals(0, contentNodeFacade.getUserFavouriteCount(userId2));

		List<ContentNodeOverviewTO> favourites = contentNodeFacade.getUserFavourite(userId1, 0, 10);
		assertEquals(1, favourites.size());
		assertEquals(contentNode1, favourites.get(0).getId());
	}

	@Test
	public void testModify() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");
		Long contentNode1 = mockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		tags = new HashSet<>();
		tags.add("new1");
		tags.add("new2");

		contentNodeFacade.modify(contentNode1, "newNameAfterModify", tags, false, LocalDateTime.of(1980, 2, 3, 10, 15));
		ContentNodeTO contentNode = contentNodeFacade.getByID(contentNode1);

		assertEquals("newNameAfterModify", contentNode.getName());
		for (ContentTagOverviewTO t : contentNode.getContentTags())
			tags.remove(t.getName());
		assertTrue(tags.isEmpty());
		assertEquals(new Long(30L), contentNode.getContentID());
		assertEquals(userId1, contentNode.getAuthor().getId());
		assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 1, contentNode.getContentReaderID());
		assertEquals(LocalDateTime.of(1980, 2, 3, 10, 15), contentNode.getCreationDate());
		assertFalse(contentNode.isPublicated());

	}

	@Test
	public void testMoveContent() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);
		Long nodeId2 = mockService.createMockRootNode(2);
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		mockService.createMockContentNode(32L, null, nodeId2, userId1, 1);

		assertEquals(1, contentNodeFacade.getCountByNode(nodeId1));
		assertEquals(1, contentNodeFacade.getCountByNode(nodeId2));

		contentNodeFacade.moveContent(nodeId2, contentNode1);

		assertEquals(0, contentNodeFacade.getCountByNode(nodeId1));
		assertEquals(2, contentNodeFacade.getCountByNode(nodeId2));
	}

	@Test
	public void testDeleteByContentId() {
		assertEquals(0, contentNodeFacade.getCount());

		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		Long contentNode2 = mockService.createMockContentNode(31L, null, nodeId1, userId1, 2);
		Long contentNode3 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 3);

		assertEquals(3, contentNodeFacade.getCount());

		contentNodeFacade.deleteByContentId(30L);

		assertEquals(2, contentNodeFacade.getCount());

		assertNull(contentNodeFacade.getByID(contentNode1));
		assertNotNull(contentNodeFacade.getByID(contentNode2));
		assertNotNull(contentNodeFacade.getByID(contentNode3));

	}

	@Test
	public void testDeleteByContentNodeId() {
		assertEquals(0, contentNodeFacade.getCount());

		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		Long contentNode2 = mockService.createMockContentNode(31L, null, nodeId1, userId1, 2);
		Long contentNode3 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 3);

		assertEquals(3, contentNodeFacade.getCount());

		contentNodeFacade.deleteByContentNodeId(contentNode2);

		assertEquals(2, contentNodeFacade.getCount());

		assertNotNull(contentNodeFacade.getByID(contentNode1));
		assertNull(contentNodeFacade.getByID(contentNode2));
		assertNotNull(contentNodeFacade.getByID(contentNode3));
	}

	@Test
	public void testSave() {
		assertEquals(0, contentNodeFacade.getCount());

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

		ContentNodeTO contentNodeByID = contentNodeFacade.getByID(contentNodeId);
		assertNotNull(contentNodeByID);
		assertEquals(moduleId, contentNodeByID.getContentReaderID());
		assertEquals(contentId, contentNodeByID.getContentID());
		for (ContentTagOverviewTO t : contentNodeByID.getContentTags())
			tags.remove(t.getName());
		assertTrue(tags.isEmpty());
		assertEquals(name, contentNodeByID.getName());
		assertEquals(userId, contentNodeByID.getAuthor().getId());
		assertEquals(nodeId2, contentNodeByID.getParent().getId());

		assertEquals(1, contentNodeFacade.getCount());
	}

	@Test
	public void testGetByNode() {
		assertEquals(0, contentNodeFacade.getCount());

		Long userId1 = mockService.createMockUser(1);
		Long userId2 = mockService.createMockUser(2);
		Long nodeId1 = mockService.createMockRootNode(1);
		Long nodeId2 = mockService.createMockRootNode(2);

		Set<String> tags = new HashSet<>();

		mockService.createMockContentNode(20L, tags, nodeId1, userId1, 1);
		Long contentNode2 = mockService.createMockContentNode(30L, tags, nodeId2, userId1, 2);
		Long contentNode3 = mockService.createMockContentNode(25L, tags, nodeId2, userId2, 3);

		assertEquals(1, contentNodeFacade.getCountByNode(nodeId1));
		assertEquals(2, contentNodeFacade.getCountByNode(nodeId2));

		List<ContentNodeOverviewTO> contentNodesByNode = contentNodeFacade.getByNode(nodeId2, 0, 10);
		assertEquals(2, contentNodesByNode.size());

		ContentNodeOverviewTO contentNodeByNode = contentNodesByNode.get(0);
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

		assertEquals(3, contentNodeFacade.getCount());
	}

	@Test
	public void testGetByTag() {
		assertEquals(0, contentNodeFacade.getCount());

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

		ContentTagOverviewTO tag = contentTagFacade.getTagByName("něco");
		assertNotNull(tag);
		assertEquals("něco", tag.getName());
		assertEquals(1, contentTagFacade.getTagContentsCount(tag.getId()));
		assertEquals(1, contentNodeFacade.getCountByTag(tag.getId()));

		List<ContentNodeOverviewTO> contentNodesByTag = contentNodeFacade.getByTag(tag.getId(), 0, 10);
		assertEquals(1, contentNodesByTag.size());
		ContentNodeOverviewTO contentNodeByTag = contentNodesByTag.get(0);
		assertEquals(contentNode3, contentNodeByTag.getId());
		assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 3, contentNodeByTag.getContentReaderID());
		assertEquals(new Long(25L), contentNodeByTag.getContentID());
		assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 3, contentNodeByTag.getName());
		assertEquals(userId2, contentNodeByTag.getAuthor().getId());
		assertEquals(nodeId2, contentNodeByTag.getParent().getId());

		tag = contentTagFacade.getTagByName("pokusy");
		assertNotNull(tag);
		assertEquals("pokusy", tag.getName());
		assertEquals(3, contentTagFacade.getTagContentsCount(tag.getId()));
		assertEquals(3, contentNodeFacade.getCountByTag(tag.getId()));

		assertEquals(3, contentNodeFacade.getCount());

	}

}

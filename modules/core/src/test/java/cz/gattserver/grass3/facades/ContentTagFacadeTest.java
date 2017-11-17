package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.test.GrassFacadeTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ContentTagFacadeTest extends GrassFacadeTest {

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Test
	public void testGetContentTagsForOverview() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagFacade.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagFacade.saveTags(tags, contentNode2);

		List<ContentTagOverviewTO> tagsTOs = contentTagFacade.getTagsForOverviewOrderedByName();
		assertEquals(2, tagsTOs.size());

		ContentTagOverviewTO tagTO = tagsTOs.get(0);
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagFacade.getTagContentsCount(tagTO.getId()));

		tagTO = tagsTOs.get(1);
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagFacade.getTagContentsCount(tagTO.getId()));
	}

	@Test
	public void testGetContentTagByName() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagFacade.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagFacade.saveTags(tags, contentNode2);

		ContentTagOverviewTO tagTO = contentTagFacade.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagFacade.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagFacade.getTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagFacade.getTagContentsCount(tagTO.getId()));
	}

	@Test
	public void testSaveTags() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagFacade.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagFacade.saveTags(tags, contentNode2);

		ContentTagOverviewTO tagTO = contentTagFacade.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagFacade.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagFacade.getTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagFacade.getTagContentsCount(tagTO.getId()));

		tags = new HashSet<>();
		contentTagFacade.saveTags(tags, contentNode2);

		tagTO = contentTagFacade.getTagByName("novinky");
		assertEquals(1, contentTagFacade.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagFacade.getTagByName("pokusy");
		assertNull(tagTO);
	}

	@Test
	public void testGetTagsContentsCountsGroups() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		mockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		tags.add("tag2");
		tags.add("tag3");
		mockService.createMockContentNode(32L, tags, nodeId1, userId1, 2);

		tags.add("tag4");
		mockService.createMockContentNode(33L, tags, nodeId1, userId1, 3);
		mockService.createMockContentNode(34L, tags, nodeId1, userId1, 4);

		tags.add("tag5");
		mockService.createMockContentNode(35L, tags, nodeId1, userId1, 5);

		List<Integer> list = contentTagFacade.getTagsContentsCountsGroups();
		assertEquals(4, list.size());
		assertEquals(1, list.get(0).intValue());
		assertEquals(3, list.get(1).intValue());
		assertEquals(4, list.get(2).intValue());
		assertEquals(5, list.get(3).intValue());
	}

}

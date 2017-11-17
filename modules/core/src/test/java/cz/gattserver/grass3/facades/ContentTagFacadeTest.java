package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;
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

		List<ContentTagOverviewDTO> tagsTOs = contentTagFacade.getContentTagsForOverviewOrderedByName();
		assertEquals(2, tagsTOs.size());

		ContentTagOverviewDTO tagTO = tagsTOs.get(0);
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagFacade.getContentNodesCount(tagTO.getId()));

		tagTO = tagsTOs.get(1);
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagFacade.getContentNodesCount(tagTO.getId()));
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

		ContentTagOverviewDTO tagTO = contentTagFacade.getContentTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagFacade.getContentNodesCount(tagTO.getId()));

		tagTO = contentTagFacade.getContentTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagFacade.getContentNodesCount(tagTO.getId()));
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

		ContentTagOverviewDTO tagTO = contentTagFacade.getContentTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagFacade.getContentNodesCount(tagTO.getId()));

		tagTO = contentTagFacade.getContentTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagFacade.getContentNodesCount(tagTO.getId()));

		tags = new HashSet<>();
		contentTagFacade.saveTags(tags, contentNode2);

		tagTO = contentTagFacade.getContentTagByName("novinky");
		assertEquals(1, contentTagFacade.getContentNodesCount(tagTO.getId()));

		tagTO = contentTagFacade.getContentTagByName("pokusy");
		assertNull(tagTO);
	}

}

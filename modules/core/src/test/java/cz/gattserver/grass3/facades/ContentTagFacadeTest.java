package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.test.GrassFacadeTest;
import cz.gattserver.grass3.test.MockUtils;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class ContentTagFacadeTest extends GrassFacadeTest {

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	@Test
	public void testGetTagsForOverviewOrderedByName() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagFacade.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagFacade.saveTags(tags, contentNode2);

		tags.add("atrapa");
		Long contentNode3 = mockService.createMockContentNode(33L, null, nodeId1, userId1, 3);
		contentTagFacade.saveTags(tags, contentNode3);

		Set<ContentTagOverviewTO> tagsTOs = contentTagFacade.getTagsForOverviewOrderedByName();
		assertEquals(3, tagsTOs.size());

		Iterator<ContentTagOverviewTO> iter = tagsTOs.iterator();
		assertEquals("atrapa", iter.next().getName());
		assertEquals("novinky", iter.next().getName());
		assertEquals("pokusy", iter.next().getName());
	}

	@Test
	public void testGetTagContentsCount() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagFacade.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = mockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagFacade.saveTags(tags, contentNode2);

		tags.add("atrapa");
		Long contentNode3 = mockService.createMockContentNode(33L, null, nodeId1, userId1, 3);
		contentTagFacade.saveTags(tags, contentNode3);

		Set<ContentTagOverviewTO> tagsTOs = contentTagFacade.getTagsForOverviewOrderedByName();
		assertEquals(3, tagsTOs.size());

		Iterator<ContentTagOverviewTO> iter = tagsTOs.iterator();
		assertEquals(1, contentTagFacade.getTagContentsCount(iter.next().getId()));
		assertEquals(3, contentTagFacade.getTagContentsCount(iter.next().getId()));
		assertEquals(2, contentTagFacade.getTagContentsCount(iter.next().getId()));
	}

	@Test(expected = NullPointerException.class)
	public void testSaveTags_fail3() {
		contentTagFacade.saveTags(new ArrayList<>(), null);
	}

	@Test
	public void testGetTagByName() {
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

	@Test(expected = NullPointerException.class)
	public void testGetTagByName_fail() {
		contentTagFacade.getTagByName(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTagByName_fail2() {
		contentTagFacade.getTagByName("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTagByName_fail3() {
		contentTagFacade.getTagByName(" ");
	}

	@Test
	public void testGetTagById() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");
		mockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		ContentTagOverviewTO tagTO = contentTagFacade.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		tagTO = contentTagFacade.getTagById(tagTO.getId());
		assertEquals("novinky", tagTO.getName());

		tagTO = contentTagFacade.getTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		tagTO = contentTagFacade.getTagById(tagTO.getId());
		assertEquals("pokusy", tagTO.getName());
	}

	@Test
	public void testTagsDelete_bySaveTags() {
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
	public void testTagsDelete_byContentDelete() {
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

		contentNodeFacade.deleteByContentId(MockUtils.MOCK_CONTENTNODE_MODULE + 2, 32L);

		tagTO = contentTagFacade.getTagByName("novinky");
		assertEquals(1, contentTagFacade.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagFacade.getTagByName("pokusy");
		assertNull(tagTO);
	}

	@Test
	public void testTagsDelete_byContentDelete2() {
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

		contentNodeFacade.deleteByContentNodeId(contentNode2);

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

	@Test
	public void testGetTagsContentsCountsMap() {
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

		Map<Long, Integer> map = contentTagFacade.getTagsContentsCountsMap();
		assertEquals(5, map.size());
		Iterator<Integer> iter = map.values().iterator();
		assertEquals(1, iter.next().intValue());
		assertEquals(3, iter.next().intValue());
		assertEquals(4, iter.next().intValue());
		assertEquals(4, iter.next().intValue());
		assertEquals(5, iter.next().intValue());
	}

	@Test
	public void testCreateTagsCloud() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("a");
		mockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		tags.add("d");
		tags.add("e");
		mockService.createMockContentNode(32L, tags, nodeId1, userId1, 2);

		tags.add("b");
		mockService.createMockContentNode(33L, tags, nodeId1, userId1, 3);
		mockService.createMockContentNode(34L, tags, nodeId1, userId1, 4);

		tags.add("c");
		mockService.createMockContentNode(35L, tags, nodeId1, userId1, 5);

		List<ContentTagsCloudItemTO> list = contentTagFacade.createTagsCloud(20, 5);
		assertEquals(5, list.size());

		Iterator<ContentTagsCloudItemTO> iter = list.iterator();
		ContentTagsCloudItemTO item = iter.next();
		assertEquals("a", item.getName());
		assertEquals(5, item.getContentsCount().intValue());
		assertEquals(20, item.getFontSize());

		item = iter.next();
		assertEquals("b", item.getName());
		assertEquals(3, item.getContentsCount().intValue());
		assertEquals(10, item.getFontSize());

		item = iter.next();
		assertEquals("c", item.getName());
		assertEquals(1, item.getContentsCount().intValue());
		assertEquals(5, item.getFontSize());

		item = iter.next();
		assertEquals("d", item.getName());
		assertEquals(4, item.getContentsCount().intValue());
		assertEquals(15, item.getFontSize());

		item = iter.next();
		assertEquals("e", item.getName());
		assertEquals(4, item.getContentsCount().intValue());
		assertEquals(15, item.getFontSize());
	}

	@Test
	public void testCreateTagsCloud_smallFontRange() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("a");
		mockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);
		mockService.createMockContentNode(33L, tags, nodeId1, userId1, 2);

		tags.add("c");
		mockService.createMockContentNode(34L, tags, nodeId1, userId1, 3);

		tags.add("b");
		mockService.createMockContentNode(35L, tags, nodeId1, userId1, 4);

		tags.add("aa");
		mockService.createMockContentNode(36L, tags, nodeId1, userId1, 5);

		List<ContentTagsCloudItemTO> list = contentTagFacade.createTagsCloud(7, 5);
		assertEquals(4, list.size());

		Iterator<ContentTagsCloudItemTO> iter = list.iterator();
		ContentTagsCloudItemTO item = iter.next();
		assertEquals("a", item.getName());
		assertEquals(5, item.getContentsCount().intValue());
		assertEquals(7, item.getFontSize());

		item = iter.next();
		assertEquals("aa", item.getName());
		assertEquals(1, item.getContentsCount().intValue());
		assertEquals(5, item.getFontSize());

		item = iter.next();
		assertEquals("b", item.getName());
		assertEquals(2, item.getContentsCount().intValue());
		assertEquals(6, item.getFontSize());

		item = iter.next();
		assertEquals("c", item.getName());
		assertEquals(3, item.getContentsCount().intValue());
		assertEquals(7, item.getFontSize());

	}

	@Test
	public void testCreateTagsCloud_singleElement() {
		Long userId1 = mockService.createMockUser(1);
		Long nodeId1 = mockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("q");
		mockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		List<ContentTagsCloudItemTO> list = contentTagFacade.createTagsCloud(20, 5);
		assertEquals(1, list.size());

		Iterator<ContentTagsCloudItemTO> iter = list.iterator();
		ContentTagsCloudItemTO item = iter.next();
		assertEquals("q", item.getName());
		assertEquals(1, item.getContentsCount().intValue());
		assertEquals(5, item.getFontSize());
	}

	@Test
	public void testCreateTagsCloud_empty() {
		List<ContentTagsCloudItemTO> list = contentTagFacade.createTagsCloud(20, 5);
		assertEquals(0, list.size());
	}

}

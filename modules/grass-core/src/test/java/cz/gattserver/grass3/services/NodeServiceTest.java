package cz.gattserver.grass3.services;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.repositories.NodeRepository;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class NodeServiceTest extends AbstractDBUnitTest {

	@Autowired
	private NodeService nodeService;

	@Autowired
	private NodeRepository nodeRepository;

	@Test
	public void testCreateNewNode() {
		Long nodeId = nodeService.createNewNode(null, "testNode");
		NodeTO node = nodeService.getNodeByIdForDetail(nodeId);
		assertNotNull(node);
		assertEquals(nodeId, node.getId());
		assertNull(node.getParent());
		assertEquals("testNode", node.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateNewNode_fail() {
		nodeService.createNewNode(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNewNode_fail2() {
		nodeService.createNewNode(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNewNode_fail3() {
		nodeService.createNewNode(null, " ");
	}

	@Test
	public void testDeleteNode() {
		assertEquals(0, nodeService.getNodesForTree().size());
		Long nodeId1 = nodeService.createNewNode(null, "testNode");
		nodeService.createNewNode(null, "testNode2");
		assertEquals(2, nodeService.getNodesForTree().size());
		nodeService.deleteNode(nodeId1);
		assertEquals(1, nodeService.getNodesForTree().size());
	}

	@Test(expected = IllegalStateException.class)
	public void testDeleteNode_notEmpty() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode");
		nodeService.createNewNode(nodeId1, "testNode");
		nodeService.deleteNode(nodeId1);
	}

	@Test(expected = IllegalStateException.class)
	public void testDeleteNode_notEmpty2() {
		Long userId = coreMockService.createMockUser(1);
		Long nodeId = nodeService.createNewNode(null, "testNode");
		coreMockService.createMockContentNode(3L, null, nodeId, userId, 1);
		nodeService.deleteNode(nodeId);
	}

	@Test
	public void testGetNodeByIdForDetail() {
		Long nodeId0 = nodeService.createNewNode(null, "testParent");
		Long nodeId1 = nodeService.createNewNode(nodeId0, "testNode");
		NodeTO node = nodeService.getNodeByIdForDetail(nodeId1);
		assertEquals(nodeId1, node.getId());
		assertEquals("testNode", node.getName());
		assertEquals("testParent", node.getParent().getName());
	}

	@Test
	public void testGetNodeByIdForOverview() {
		Long nodeId0 = nodeService.createNewNode(null, "testParent");
		Long nodeId1 = nodeService.createNewNode(nodeId0, "testNode");
		NodeOverviewTO node = nodeService.getNodeByIdForOverview(nodeId1);
		assertEquals(nodeId1, node.getId());
		assertEquals("testNode", node.getName());
		assertEquals(nodeId0, node.getParentId());
		assertEquals("testParent", node.getParentName());
	}

	@Test
	public void testGetNodesByParentNode() {
		Long nodeId0 = nodeService.createNewNode(null, "testParent");
		nodeService.createNewNode(nodeId0, "testNode1");
		nodeService.createNewNode(nodeId0, "testNode2");
		List<NodeOverviewTO> nodes = nodeService.getNodesByParentNode(nodeId0);
		assertEquals(2, nodes.size());
		assertEquals("testNode1", nodes.get(0).getName());
		assertEquals("testNode2", nodes.get(1).getName());
	}

	@Test
	public void testGetNodesForTree() {
		Long nodeId0 = nodeService.createNewNode(null, "testParent");
		nodeService.createNewNode(nodeId0, "testNode1");
		Long nodeId1 = nodeService.createNewNode(nodeId0, "testNode2");
		nodeService.createNewNode(nodeId1, "testChild");
		List<NodeOverviewTO> nodes = nodeService.getNodesForTree();
		assertEquals(4, nodes.size());
		assertEquals("testParent", nodes.get(0).getName());
		assertEquals("testNode1", nodes.get(1).getName());
		assertEquals("testNode2", nodes.get(2).getName());
		assertEquals("testChild", nodes.get(3).getName());
	}

	@Test
	public void testGetRootNodes() {
		Long nodeId0 = nodeService.createNewNode(null, "testParent");
		nodeService.createNewNode(null, "testParent2");
		nodeService.createNewNode(nodeId0, "testNode1");
		Long nodeId1 = nodeService.createNewNode(nodeId0, "testNode2");
		nodeService.createNewNode(nodeId1, "testChild");
		List<NodeOverviewTO> nodes = nodeService.getRootNodes();
		assertEquals(2, nodes.size());
		assertEquals("testParent", nodes.get(0).getName());
		assertEquals("testParent2", nodes.get(1).getName());
	}

	@Test
	public void testIsNodeEmpty() {
		Long nodeId1 = nodeService.createNewNode(null, "nodeWithContentNode");
		Long nodeId2 = nodeService.createNewNode(null, "nodeWithSubNode");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "emptyNode");
		Long userId1 = coreMockService.createMockUser(1);
		coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		assertFalse(nodeService.isNodeEmpty(nodeId1));
		assertFalse(nodeService.isNodeEmpty(nodeId2));
		assertTrue(nodeService.isNodeEmpty(nodeId3));
	}

	@Test
	public void testMoveNode_newRoot() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(nodeId1, "testNode3");

		NodeTO nodeDTO = nodeService.getNodeByIdForDetail(nodeId2);
		assertEquals(nodeId1, nodeDTO.getParentId());
		assertEquals(nodeId1, nodeDTO.getParent().getId());
		assertNull(nodeDTO.getParent().getParentId());
		assertNull(nodeDTO.getParent().getParent());

		nodeService.moveNode(nodeId2, null);

		nodeDTO = nodeService.getNodeByIdForDetail(nodeId2);
		assertNull(nodeDTO.getParentId());
		assertNull(nodeDTO.getParent());
	}

	@Test
	public void testMoveNode_ok1() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(null, "testNode2");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "testNode3");

		nodeService.moveNode(nodeId2, nodeId1);
		assertEquals(nodeId1, nodeService.getNodeByIdForOverview(nodeId2).getParentId());

		NodeTO nodeDTO = nodeService.getNodeByIdForDetail(nodeId3);
		assertEquals(nodeId2, nodeDTO.getParentId());
		assertEquals(nodeId2, nodeDTO.getParent().getId());
		assertEquals(nodeId1, nodeDTO.getParent().getParentId());
		assertEquals(nodeId1, nodeDTO.getParent().getParent().getId());
	}

	@Test
	public void testMoveNode_ok2() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(null, "testNode2");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "testNode3");

		nodeService.moveNode(nodeId2, nodeId1);
		nodeService.moveNode(nodeId3, nodeId1);

		NodeTO nodeDTO = nodeService.getNodeByIdForDetail(nodeId3);
		assertEquals(nodeId1, nodeDTO.getParentId());
		assertEquals(nodeId1, nodeDTO.getParent().getId());
		assertNull(nodeDTO.getParent().getParent());
	}

	@Test
	public void testMoveNode_noChange() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(nodeId1, "testNode2");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "testNode3");

		nodeService.moveNode(nodeId3, nodeId2);

		NodeTO nodeDTO = nodeService.getNodeByIdForDetail(nodeId3);
		assertEquals(nodeId2, nodeDTO.getParentId());
		assertEquals(nodeId1, nodeDTO.getParent().getParentId());
		assertNull(nodeDTO.getParent().getParent().getParent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveNode_fail1() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(nodeId1, "testNode2");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "testNode3");
		nodeService.moveNode(nodeId1, nodeId3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveNode_fail2() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(nodeId1, "testNode2");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "testNode3");
		nodeService.moveNode(nodeId2, nodeId3);
	}

	@Test(expected = IllegalStateException.class)
	public void testMoveNode_dbCycle() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode1");
		Long nodeId2 = nodeService.createNewNode(nodeId1, "testNode2");
		Long nodeId3 = nodeService.createNewNode(nodeId2, "testNode3");

		Node node = nodeRepository.findById(nodeId1).orElse(null);
		Node node3 = new Node();
		node3.setId(nodeId3);
		node.setParent(node3);
		nodeRepository.save(node);

		Long nodeId4 = nodeService.createNewNode(null, "testNode4");
		nodeService.moveNode(nodeId4, nodeId3);
	}

	@Test
	public void testRenameNode() {
		Long nodeId1 = nodeService.createNewNode(null, "testNode");
		nodeService.rename(nodeId1, "newTestNode");
		assertEquals("newTestNode", nodeService.getNodeByIdForOverview(nodeId1).getName());
	}

	@Test(expected = NullPointerException.class)
	public void testRenameNode_fail() {
		nodeService.rename(1L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRenameNode_fail2() {
		nodeService.rename(1L, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRenameNode_fail3() {
		nodeService.rename(1L, " ");
	}

}

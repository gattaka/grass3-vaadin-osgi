package cz.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.test.GrassFacadeTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
public class NodeFacadeTest extends GrassFacadeTest {

	@Autowired
	private NodeFacade nodeFacade;

	@Test
	public void testCreateNewNode() {
		Long nodeId = nodeFacade.createNewNode(null, "testNode");
		NodeTO node = nodeFacade.getNodeByIdForDetail(nodeId);
		assertNotNull(node);
		assertEquals(nodeId, node.getId());
		assertNull(node.getParent());
		assertEquals("testNode", node.getName());
	}

	@Test
	public void testDeleteNode() {
		assertEquals(0, nodeFacade.getNodesForTree().size());
		Long nodeId1 = nodeFacade.createNewNode(null, "testNode");
		nodeFacade.createNewNode(null, "testNode2");
		assertEquals(2, nodeFacade.getNodesForTree().size());
		nodeFacade.deleteNode(nodeId1);
		assertEquals(1, nodeFacade.getNodesForTree().size());
	}

	@Test
	public void testGetNodeByIdForDetail() {
		Long nodeId0 = nodeFacade.createNewNode(null, "testParent");
		Long nodeId1 = nodeFacade.createNewNode(nodeId0, "testNode");
		NodeTO node = nodeFacade.getNodeByIdForDetail(nodeId1);
		assertEquals(nodeId1, node.getId());
		assertEquals("testNode", node.getName());
		assertEquals("testParent", node.getParent().getName());
	}

	@Test
	public void testGetNodeByIdForOverview() {
		Long nodeId0 = nodeFacade.createNewNode(null, "testParent");
		Long nodeId1 = nodeFacade.createNewNode(nodeId0, "testNode");
		NodeOverviewTO node = nodeFacade.getNodeByIdForOverview(nodeId1);
		assertEquals(nodeId1, node.getId());
		assertEquals("testNode", node.getName());
		assertEquals(nodeId0, node.getParentId());
		assertEquals("testParent", node.getParentName());
	}

	@Test
	public void testGetNodesByParentNode() {
		Long nodeId0 = nodeFacade.createNewNode(null, "testParent");
		nodeFacade.createNewNode(nodeId0, "testNode1");
		nodeFacade.createNewNode(nodeId0, "testNode2");
		List<NodeOverviewTO> nodes = nodeFacade.getNodesByParentNode(nodeId0);
		assertEquals(2, nodes.size());
		assertEquals("testNode1", nodes.get(0).getName());
		assertEquals("testNode2", nodes.get(1).getName());
	}

	@Test
	public void testGetNodesForTree() {
		Long nodeId0 = nodeFacade.createNewNode(null, "testParent");
		nodeFacade.createNewNode(nodeId0, "testNode1");
		Long nodeId1 = nodeFacade.createNewNode(nodeId0, "testNode2");
		nodeFacade.createNewNode(nodeId1, "testChild");
		List<NodeOverviewTO> nodes = nodeFacade.getNodesForTree();
		assertEquals(4, nodes.size());
		assertEquals("testParent", nodes.get(0).getName());
		assertEquals("testNode1", nodes.get(1).getName());
		assertEquals("testNode2", nodes.get(2).getName());
		assertEquals("testChild", nodes.get(3).getName());
	}

	@Test
	public void testGetRootNodes() {
		Long nodeId0 = nodeFacade.createNewNode(null, "testParent");
		nodeFacade.createNewNode(null, "testParent2");
		nodeFacade.createNewNode(nodeId0, "testNode1");
		Long nodeId1 = nodeFacade.createNewNode(nodeId0, "testNode2");
		nodeFacade.createNewNode(nodeId1, "testChild");
		List<NodeOverviewTO> nodes = nodeFacade.getRootNodes();
		assertEquals(2, nodes.size());
		assertEquals("testParent", nodes.get(0).getName());
		assertEquals("testParent2", nodes.get(1).getName());
	}

	@Test
	public void testIsNodeEmpty() {
		Long nodeId1 = nodeFacade.createNewNode(null, "nodeWithContentNode");
		Long nodeId2 = nodeFacade.createNewNode(null, "nodeWithSubNode");
		Long nodeId3 = nodeFacade.createNewNode(nodeId2, "emptyNode");
		Long userId1 = mockService.createMockUser(1);
		mockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		assertFalse(nodeFacade.isNodeEmpty(nodeId1));
		assertFalse(nodeFacade.isNodeEmpty(nodeId2));
		assertTrue(nodeFacade.isNodeEmpty(nodeId3));
	}

	@Test
	public void testMoveNode_ok1() {
		Long nodeId1 = nodeFacade.createNewNode(null, "testNode1");
		Long nodeId2 = nodeFacade.createNewNode(null, "testNode2");
		Long nodeId3 = nodeFacade.createNewNode(nodeId2, "testNode3");

		nodeFacade.moveNode(nodeId2, nodeId1);
		assertEquals(nodeId1, nodeFacade.getNodeByIdForOverview(nodeId2).getParentId());

		NodeTO nodeDTO = nodeFacade.getNodeByIdForDetail(nodeId3);
		assertEquals(nodeId2, nodeDTO.getParentId());
		assertEquals(nodeId2, nodeDTO.getParent().getId());
		assertEquals(nodeId1, nodeDTO.getParent().getParentId());
		assertEquals(nodeId1, nodeDTO.getParent().getParent().getId());
	}

	@Test
	public void testMoveNode_ok2() {
		Long nodeId1 = nodeFacade.createNewNode(null, "testNode1");
		Long nodeId2 = nodeFacade.createNewNode(null, "testNode2");
		Long nodeId3 = nodeFacade.createNewNode(nodeId2, "testNode3");

		nodeFacade.moveNode(nodeId2, nodeId1);
		nodeFacade.moveNode(nodeId3, nodeId1);

		NodeTO nodeDTO = nodeFacade.getNodeByIdForDetail(nodeId3);
		assertEquals(nodeId1, nodeDTO.getParentId());
		assertEquals(nodeId1, nodeDTO.getParent().getId());
		assertNull(nodeDTO.getParent().getParent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveNode_fail1() {
		Long nodeId1 = nodeFacade.createNewNode(null, "testNode1");
		Long nodeId2 = nodeFacade.createNewNode(nodeId1, "testNode2");
		Long nodeId3 = nodeFacade.createNewNode(nodeId2, "testNode3");
		nodeFacade.moveNode(nodeId1, nodeId3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveNode_fail2() {
		Long nodeId1 = nodeFacade.createNewNode(null, "testNode1");
		Long nodeId2 = nodeFacade.createNewNode(nodeId1, "testNode2");
		Long nodeId3 = nodeFacade.createNewNode(nodeId2, "testNode3");
		nodeFacade.moveNode(nodeId2, nodeId3);
	}

	@Test
	public void testRenameNode() {
		Long nodeId1 = nodeFacade.createNewNode(null, "testNode");
		nodeFacade.rename(nodeId1, "newTestNode");
		assertEquals("newTestNode", nodeFacade.getNodeByIdForOverview(nodeId1).getName());
	}

}

package cz.gattserver.grass3.facades.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.model.dao.NodeRepository;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.util.CoreMapper;

@Transactional
@Component
public class NodeFacadeImpl implements NodeFacade {

	@Autowired
	private CoreMapper mapper;

	@Autowired
	private NodeRepository nodeRepository;

	@Override
	public NodeOverviewTO getNodeByIdForOverview(Long nodeId) {
		Validate.notNull(nodeId, "'nodeId' kategorie nemůže být null");
		Node node = nodeRepository.findOne(nodeId);
		NodeOverviewTO nodeDTO = mapper.mapNodeForOverview(node);
		return nodeDTO;
	}

	@Override
	public NodeTO getNodeByIdForDetail(Long nodeId) {
		Validate.notNull(nodeId, "'nodeId' kategorie nemůže být null");
		Node node = nodeRepository.findOne(nodeId);
		NodeTO nodeDTO = mapper.mapNodeForDetail(node);
		return nodeDTO;
	}

	@Override
	public List<NodeOverviewTO> getRootNodes() {
		List<Node> rootNodes = nodeRepository.findByParentIsNull();
		List<NodeOverviewTO> rootNodesDTOs = mapper.mapNodesForOverview(rootNodes);
		return rootNodesDTOs;
	}

	@Override
	public List<NodeOverviewTO> getNodesForTree() {
		List<Node> nodes = nodeRepository.findAll(new Sort("id"));
		List<NodeOverviewTO> nodeDTOs = mapper.mapNodesForOverview(nodes);
		return nodeDTOs;
	}

	@Override
	public List<NodeOverviewTO> getNodesByParentNode(Long parentId) {
		List<Node> childrenNodes = nodeRepository.findByParentId(parentId);
		List<NodeOverviewTO> childrenNodesDTOs = mapper.mapNodesForOverview(childrenNodes);
		return childrenNodesDTOs;
	}

	@Override
	public Long createNewNode(Long parentId, String name) {
		Validate.notNull(name, "'name' kategorie nemůže být null");
		Node node = new Node();
		node.setName(name.trim());
		node = nodeRepository.save(node);

		// if (parentId != null) {
		// Node parent = new Node();
		// parent.setId(parentId);
		// node.setParent(parent);
		// }

		if (parentId != null) {
			Node parentEntity = nodeRepository.findOne(parentId);
			parentEntity.getSubNodes().add(node);
			parentEntity = nodeRepository.save(parentEntity);
			if (parentEntity == null)
				return null;

			node.setParent(parentEntity);
			node = nodeRepository.save(node);
			if (node == null)
				return null;
		}

		return node.getId();
	}

	@Override
	public void moveNode(Long nodeId, Long newParentId) {
		Validate.notNull(nodeId, "'nodeId' kategorie nemůže být null");

		Node newParentEntity = newParentId == null ? null : nodeRepository.findOne(newParentId);
		Node nodeEntity = nodeRepository.findOne(nodeId);

		// beze změn
		if (Objects.equals(nodeEntity.getParent(), newParentEntity))
			return;

		// zamezí vkládání předků do potomků - projde postupně všechny předky
		// cílové kategorie a pokud narazí na moje id, pak jsem předkem cílové
		// kategorie, což je špatně
		if (newParentEntity != null) {
			Node cycleCheckParent = newParentEntity;
			// začínám od předka newParent - tohle je schválně, umožní mi to se
			// pak ptát na id newParent - pokud totiž narazím na newParent id,
			// pak je v DB cykl
			cycleCheckParent = cycleCheckParent.getParent();
			while (cycleCheckParent != null) {
				if (cycleCheckParent.getId() == newParentId)
					throw new IllegalStateException("V grafu kategorií byl nalezen cykl");
				if (cycleCheckParent.getId() == nodeId)
					throw new IllegalArgumentException("Nelze vkládat předka do potomka");
				cycleCheckParent = cycleCheckParent.getParent();
			}
		}

		if (nodeEntity.getParent() != null) {
			nodeEntity.getParent().getSubNodes().remove(nodeEntity);
			nodeRepository.save(nodeEntity.getParent());
		}

		nodeRepository.flush();

		if (newParentId != null) {
			newParentEntity.getSubNodes().add(nodeEntity);
			newParentEntity = nodeRepository.save(newParentEntity);
			nodeEntity.setParent(newParentEntity);
		} else {
			nodeEntity.setParent(null);
		}

		nodeRepository.save(nodeEntity);
	}

	@Override
	public void deleteNode(Long nodeId) {
		Validate.notNull(nodeId, "'nodeId' kategorie nemůže být null");
		Node nodeEntity = nodeRepository.findOne(nodeId);
		Node parent = nodeEntity.getParent();
		if (parent != null) {
			parent.getSubNodes().remove(nodeEntity);
			parent = nodeRepository.save(parent);
		}
		nodeRepository.delete(nodeId);
	}

	@Override
	public void rename(Long nodeId, String newName) {
		Validate.notNull(nodeId, "'nodeId' kategorie nemůže být null");
		Validate.notNull(newName, "'newName' kategorie nemůže být null");
		nodeRepository.rename(nodeId, newName);
	}

	@Override
	public boolean isNodeEmpty(Long nodeId) {
		Node n = nodeRepository.findOne(nodeId);
		return n.getContentNodes().size() + n.getSubNodes().size() == 0;
		// Validate.notNull(nodeId, "'nodeId' kategorie nemůže být null");
		// Integer count = nodeRepository.countAllSubNodes(nodeId);
		// return count.equals(0);
	}

}

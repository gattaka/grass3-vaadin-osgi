package cz.gattserver.grass3.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dao.NodeRepository;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.NodeTreeDTO;
import cz.gattserver.grass3.model.util.CoreMapper;

@Transactional
@Component
public class NodeFacadeImpl implements NodeFacade {

	@Autowired
	private CoreMapper mapper;

	@Autowired
	private NodeRepository nodeRepository;

	@Override
	public NodeDTO getNodeByIdForOverview(Long id) {
		Node node = nodeRepository.findOne(id);
		if (node == null)
			return null;
		NodeDTO nodeDTO = mapper.mapNodeForOverview(node);
		return nodeDTO;
	}

	@Override
	public NodeDTO getNodeByIdForDetail(Long id) {
		Node node = nodeRepository.findOne(id);
		if (node == null)
			return null;
		NodeDTO nodeDTO = mapper.mapNodeForDetail(node);
		return nodeDTO;
	}

	@Override
	public List<NodeDTO> getRootNodes() {
		List<Node> rootNodes = nodeRepository.findByParentIsNull();
		if (rootNodes == null) {
			return null;
		}
		List<NodeDTO> rootNodesDTOs = mapper.mapNodesForOverview(rootNodes);
		return rootNodesDTOs;
	}

	@Override
	public List<NodeTreeDTO> getNodesForTree() {
		List<Node> nodes = nodeRepository.findAll();
		if (nodes == null) {
			return null;
		}
		List<NodeTreeDTO> nodeDTOs = mapper.mapNodesForTree(nodes);
		return nodeDTOs;
	}

	@Override
	public List<NodeDTO> getNodesByParentNode(NodeDTO parent) {
		List<Node> childrenNodes = nodeRepository.findByParentId(parent.getId());

		if (childrenNodes == null) {
			return null;
		}

		List<NodeDTO> childrenNodesDTOs = mapper.mapNodesForOverview(childrenNodes);
		return childrenNodesDTOs;
	}

	@Override
	public Long createNewNode(Long parent, String name) {
		Node node = new Node();
		node.setName(name.trim());
		node = nodeRepository.save(node);
		if (node == null)
			return null;

		if (parent != null) {
			Node parentEntity = nodeRepository.findOne(parent);
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
		Node newParentEntity = newParentId == null ? null : nodeRepository.findOne(newParentId);
		Node nodeEntity = nodeRepository.findOne(nodeId);

		// beze změn
		if (nodeEntity.getParent().equals(newParentEntity))
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
	public void deleteNode(Long node) {
		Node nodeEntity = nodeRepository.findOne(node);
		Node parent = nodeEntity.getParent();
		if (parent != null) {
			parent.getSubNodes().remove(nodeEntity);
			parent = nodeRepository.save(parent);
		}
		nodeRepository.delete(node);
	}

	@Override
	public boolean rename(Long node, String newName) {
		Node entity = nodeRepository.findOne(node);
		if (entity == null)
			return false;
		entity.setName(newName);
		return nodeRepository.save(entity) != null;
	}

	@Override
	public boolean isEmpty(Long node) {
		Node n = nodeRepository.findOne(node);
		return n.getContentNodes().size() + n.getSubNodes().size() == 0;
	}

}

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
	public boolean moveNode(Long node, Long newParent) {
		// zamezí vkládání předků do potomků - projde postupně všechny předky
		// cílové kategorie a pokud narazí na moje id, pak jsem předkem cílové
		// kategorie, což je špatně
		Node parent = newParent == null ? null : nodeRepository.findOne(newParent);
		if (parent != null) {
			// začínám od předka newParent - tohle je schválně, umožní mi to se
			// pak ptát na id newParent - pokud totiž narazím na newParent id,
			// pak je v DB cykl
			parent = parent.getParent();
			while (parent != null) {
				if (parent.getId() == newParent)
					return false; // v DB je cykl
				if (parent.getId() == node)
					return false; // vkládám do potomka
				parent = parent.getParent();
			}
		}

		Node nodeEntity = nodeRepository.findOne(node);

		if (nodeEntity.getParent() != null) {
			nodeEntity.getParent().getSubNodes().remove(nodeEntity);
			Node oldParentEntity = nodeRepository.save(nodeEntity.getParent());
			if (oldParentEntity == null)
				return false;
		}

		if (newParent != null) {
			Node newParentEntity = nodeRepository.findOne(newParent);
			newParentEntity.getSubNodes().add(nodeEntity);
			newParentEntity = nodeRepository.save(newParentEntity);
			if (newParentEntity == null)
				return false;

			nodeEntity.setParent(newParentEntity);
		} else {
			nodeEntity.setParent(null);
		}

		nodeEntity = nodeRepository.save(nodeEntity);
		if (nodeEntity == null)
			return false;

		return true;
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

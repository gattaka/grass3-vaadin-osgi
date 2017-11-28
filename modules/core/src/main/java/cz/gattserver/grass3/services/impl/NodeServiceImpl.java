package cz.gattserver.grass3.services.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.repositories.NodeRepository;
import cz.gattserver.grass3.model.util.CoreMapper;
import cz.gattserver.grass3.services.NodeService;

@Transactional
@Service
public class NodeServiceImpl implements NodeService {

	@Autowired
	private CoreMapper mapper;

	@Autowired
	private NodeRepository nodeRepository;

	@Override
	public NodeOverviewTO getNodeByIdForOverview(long nodeId) {
		Node node = nodeRepository.findOne(nodeId);
		NodeOverviewTO nodeDTO = mapper.mapNodeForOverview(node);
		return nodeDTO;
	}

	@Override
	public NodeTO getNodeByIdForDetail(long nodeId) {
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
	public List<NodeOverviewTO> getNodesByParentNode(long parentId) {
		List<Node> childrenNodes = nodeRepository.findByParentId(parentId);
		List<NodeOverviewTO> childrenNodesDTOs = mapper.mapNodesForOverview(childrenNodes);
		return childrenNodesDTOs;
	}

	@Override
	public long createNewNode(Long parentId, String name) {
		Validate.notBlank(name, "'name' kategorie nemůže být prázdný");
		Node node = new Node();
		node.setName(name.trim());

		if (parentId != null) {
			Node parentEntity = new Node();
			parentEntity.setId(parentId);
			node.setParent(parentEntity);
		}

		node = nodeRepository.save(node);
		return node.getId();
	}

	@Override
	public void moveNode(long nodeId, Long newParentId) {
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

			nodeEntity.setParent(newParentEntity);
		} else {
			nodeEntity.setParent(null);
		}

		nodeRepository.save(nodeEntity);
	}

	@Override
	public void deleteNode(long nodeId) {
		int countContents = nodeRepository.countContentNodes(nodeId);
		int countSubNodes = nodeRepository.countSubNodes(nodeId);
		if (countContents + countSubNodes > 0)
			throw new IllegalStateException("Nelze mazat kategorii, ve které existují podkategorie nebo obsahy");
		nodeRepository.delete(nodeId);
	}

	@Override
	public void rename(long nodeId, String newName) {
		Validate.notBlank(newName, "'newName' kategorie nemůže být prázdný");
		nodeRepository.rename(nodeId, newName);
	}

	@Override
	public boolean isNodeEmpty(long nodeId) {
		int contentNodesCount = nodeRepository.countContentNodes(nodeId);
		int subNodesCount = nodeRepository.countSubNodes(nodeId);
		return contentNodesCount + subNodesCount == 0;
	}

}
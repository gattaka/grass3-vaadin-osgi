package cz.gattserver.grass3.facades.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.INodeFacade;
import cz.gattserver.grass3.model.dao.NodeRepository;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.util.Mapper;

@Transactional
@Component("nodeFacade")
public class NodeFacadeImpl implements INodeFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Autowired
	private NodeRepository nodeRepository;

	/**
	 * Získá kategorii dle id
	 */
	public NodeDTO getNodeByIdForOverview(Long id) {
		Node node = nodeRepository.findOne(id);
		if (node == null)
			return null;
		NodeDTO nodeDTO = mapper.mapNodeForOverview(node);
		return nodeDTO;
	}

	/**
	 * Získá kategorii dle id a namapuje jí, aby se dala použít v detailu
	 * kategorie
	 */
	public NodeDTO getNodeByIdForDetail(Long id) {
		Node node = nodeRepository.findOne(id);
		if (node == null)
			return null;
		NodeDTO nodeDTO = mapper.mapNodeForDetail(node);
		return nodeDTO;
	}

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeDTO> getRootNodes() {
		List<Node> rootNodes = nodeRepository.findByParentIsNull();
		if (rootNodes == null) {
			return null;
		}
		List<NodeDTO> rootNodesDTOs = mapper.mapNodesForOverview(rootNodes);
		return rootNodesDTOs;
	}

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeDTO> getNodesByParentNode(NodeDTO parent) {
		List<Node> childrenNodes = nodeRepository.findByParentId(parent.getId());

		if (childrenNodes == null) {
			return null;
		}

		List<NodeDTO> childrenNodesDTOs = mapper.mapNodesForOverview(childrenNodes);
		return childrenNodesDTOs;
	}

	/**
	 * Založí novou kategorii
	 * 
	 * @param parent
	 *            pakliže je kategorii vkládána do jiné kategorie, je vyplněn
	 *            její předek. Pokud je kategorie vkládána přímo do kořene
	 *            sekce, je tento argument <code>null</code>
	 * @param name
	 *            jméno nové kategorie
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean createNewNode(NodeDTO parent, String name) {

		Node node = new Node();
		node.setName(name);
		node = nodeRepository.save(node);
		if (node == null)
			return false;

		if (parent != null) {
			Node parentEntity = nodeRepository.findOne(parent.getId());
			parentEntity.getSubNodes().add(node);
			parentEntity = nodeRepository.save(parentEntity);
			if (parentEntity == null)
				return false;

			node.setParent(parentEntity);
			node = nodeRepository.save(node);
			if (node == null)
				return false;
		}

		return true;
	}

	/**
	 * Přesune kategorii
	 * 
	 * @param node
	 *            kategorie k přesunu
	 * @param newParent
	 *            nový předek, do kterého má být kategorie přesunuta, nebo
	 *            <code>null</code> pokud má být přesunuta do kořene sekce
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean moveNode(NodeDTO node, NodeDTO newParent) {

		// zamezí vkládání předků do potomků - projde postupně všechny předky
		// cílové kategorie a pokud narazí na moje id, pak jsem předkem cílové
		// kategorie, což je špatně
		Node parent = newParent == null ? null : nodeRepository.findOne(newParent.getId());
		if (parent != null) {
			// začínám od předka newParent - tohle je schválně, umožní mi to se
			// pak ptát na id newParent - pokud totiž narazím na newParent id,
			// pak je v DB cykl
			parent = parent.getParent();
			while (parent != null) {
				if (parent.getId() == newParent.getId())
					return false; // v DB je cykl
				if (parent.getId() == node.getId())
					return false; // vkládám do potomka
				parent = parent.getParent();
			}
		}

		Node nodeEntity = nodeRepository.findOne(node.getId());

		if (nodeEntity.getParent() != null) {
			nodeEntity.getParent().getSubNodes().remove(nodeEntity);
			Node oldParentEntity = nodeRepository.save(nodeEntity.getParent());
			if (oldParentEntity == null)
				return false;
		}

		if (newParent != null) {
			Node newParentEntity = nodeRepository.findOne(newParent.getId());
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

	/**
	 * Smaže kategorii, pokud je prázdná
	 * 
	 * @param node
	 *            kategorie ke smazání
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean deleteNode(NodeDTO node) {

		Node nodeEntity = nodeRepository.findOne(node.getId());
		Node parent = nodeEntity.getParent();
		if (parent != null) {
			parent.getSubNodes().remove(nodeEntity);
			parent = nodeRepository.save(parent);
		}

		nodeRepository.delete(node.getId());
		return true;
	}

	/**
	 * Přejmenuje kategorii
	 * 
	 * @param node
	 *            kategorie k přejmenování
	 * @param newName
	 *            nový název
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean rename(NodeDTO node, String newName) {

		Node entity = nodeRepository.findOne(node.getId());

		if (entity == null)
			return false;

		entity.setName(newName);

		return nodeRepository.save(entity) != null;
	}

	@Override
	public boolean isEmpty(NodeDTO node) {
		Node n = nodeRepository.findOne(node.getId());
		return n.getContentNodes().size() + n.getSubNodes().size() == 0;
	}
}

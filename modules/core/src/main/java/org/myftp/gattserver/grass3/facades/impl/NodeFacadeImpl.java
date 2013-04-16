package org.myftp.gattserver.grass3.facades.impl;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.model.dao.NodeDAO;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.stereotype.Component;

@Component("nodeFacade")
public class NodeFacadeImpl implements INodeFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Resource(name = "nodeDAO")
	private NodeDAO nodeDAO;

	/**
	 * Získá kategorii dle id
	 */
	public NodeDTO getNodeById(Long id) {

		Node node = nodeDAO.findByID(id);
		if (node == null)
			return null;
		NodeDTO nodeDTO = mapper.map(node);
		nodeDAO.closeSession();

		return nodeDTO;
	}

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeDTO> getRootNodes() {

		List<Node> rootNodes = nodeDAO.findRoots();

		if (rootNodes == null) {
			nodeDAO.closeSession();
			return null;
		}

		List<NodeDTO> rootNodesDTOs = mapper
				.mapNodeCollectionForLinks(rootNodes);

		nodeDAO.closeSession();
		return rootNodesDTOs;
	}

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeDTO> getNodesByParentNode(NodeDTO parent) {

		List<Node> childrenNodes = nodeDAO.findNodesByParent(parent.getId());

		if (childrenNodes == null) {
			nodeDAO.closeSession();
			return null;
		}

		List<NodeDTO> childrenNodesDTOs = mapper
				.mapNodeCollection(childrenNodes);

		nodeDAO.closeSession();
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
		return nodeDAO.createNewNode(node,
				parent == null ? null : parent.getId());

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
		Node parent = newParent == null ? null : nodeDAO.findByID(newParent
				.getId());
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

		return nodeDAO.moveNode(node.getId(), newParent == null ? null
				: newParent.getId());

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
		return nodeDAO.delete(node.getId());
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

		Node entity = nodeDAO.findByID(node.getId());
		nodeDAO.closeSession();

		if (entity == null)
			return false;

		entity.setName(newName);

		return nodeDAO.merge(entity);
	}
}

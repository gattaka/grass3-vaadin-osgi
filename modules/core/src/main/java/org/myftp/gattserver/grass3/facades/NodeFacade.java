package org.myftp.gattserver.grass3.facades;

import java.util.List;

import org.myftp.gattserver.grass3.model.dao.NodeDAO;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum NodeFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	/**
	 * Získá kategorii dle id
	 */
	public NodeDTO getNodeById(Long id) {
		NodeDAO dao = new NodeDAO();

		Node node = dao.findByID(id);
		if (node == null)
			return null;
		NodeDTO nodeDTO = mapper.map(node);
		dao.closeSession();

		return nodeDTO;
	}

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeDTO> getRootNodes() {
		NodeDAO dao = new NodeDAO();
		List<Node> rootNodes = dao.findRoots();

		if (rootNodes == null) {
			dao.closeSession();
			return null;
		}

		List<NodeDTO> rootNodesDTOs = mapper.mapNodeCollection(rootNodes);

		dao.closeSession();
		return rootNodesDTOs;
	}

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeDTO> getNodesByParentNode(NodeDTO parent) {

		NodeDAO dao = new NodeDAO();
		List<Node> childrenNodes = dao.findNodesByParent(parent.getId());

		if (childrenNodes == null) {
			dao.closeSession();
			return null;
		}

		List<NodeDTO> childrenNodesDTOs = mapper
				.mapNodeCollection(childrenNodes);

		dao.closeSession();
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

		NodeDAO dao = new NodeDAO();
		Node node = new Node();
		node.setName(name);
		return dao.createNewNode(node, parent == null ? null : parent.getId());

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

		NodeDAO dao = new NodeDAO();

		// zamezí vkládání předků do potomků - projde postupně všechny předky
		// cílové kategorie a pokud narazí na moje id, pak jsem předkem cílové
		// kategorie, což je špatně
		Node parent = newParent == null ? null : dao
				.findByID(newParent.getId());
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

		return dao.moveNode(node.getId(),
				newParent == null ? null : newParent.getId());

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
		NodeDAO dao = new NodeDAO();
		return dao.delete(node.getId());
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

		NodeDAO dao = new NodeDAO();
		Node entity = dao.findByID(node.getId());
		dao.closeSession();

		if (entity == null)
			return false;

		entity.setName(newName);

		return dao.merge(entity);
	}
}

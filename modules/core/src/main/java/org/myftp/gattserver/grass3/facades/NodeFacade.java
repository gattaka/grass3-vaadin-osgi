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
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeDTO> getRootNodes() {
		NodeDAO dao = new NodeDAO();
		List<Node> rootNodes = dao.findRoots();

		if (rootNodes == null)
			return null;

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

		if (childrenNodes == null)
			return null;

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

		if (parent != null) {
			Node parentNode = dao.findByID(parent.getId());

			// pokud se kategorie měla někam vložit a cíl neexistuje, je to
			// chyba
			if (parentNode == null) {
				dao.closeSession();
				return false;
			} else {
				node.setParent(parentNode);
				dao.closeSession();
			}
		}

		node.setName(name);

		if (dao.save(node) == null)
			return false;
		else
			return true;
	}
}

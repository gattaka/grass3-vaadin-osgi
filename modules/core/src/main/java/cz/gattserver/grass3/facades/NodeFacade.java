package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.NodeTreeDTO;

public interface NodeFacade {

	/**
	 * Získá kategorii dle id
	 */
	public NodeDTO getNodeByIdForOverview(Long id);

	/**
	 * Získá kategorii dle id a namapuje jí, aby se dala použít v detailu
	 * kategorie
	 */
	public NodeDTO getNodeByIdForDetail(Long id);

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeDTO> getRootNodes();

	/**
	 * Získá všechny kategorie pro zobrazení ve stromu
	 */
	public List<NodeTreeDTO> getNodesForTree();

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeDTO> getNodesByParentNode(NodeDTO parent);

	/**
	 * Založí novou kategorii
	 * 
	 * @param parent
	 *            pakliže je kategorii vkládána do jiné kategorie, je vyplněn
	 *            její předek. Pokud je kategorie vkládána přímo do kořene
	 *            sekce, je tento argument <code>null</code>
	 * @param name
	 *            jméno nové kategorie
	 * @return id kategorie pokud se přidání zdařilo, jinak <code>null</code>
	 */
	public Long createNewNode(NodeDTO parent, String name);

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
	public boolean moveNode(NodeDTO node, NodeDTO newParent);

	/**
	 * Smaže kategorii, pokud je prázdná
	 * 
	 * @param node
	 *            kategorie ke smazání
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean deleteNode(NodeDTO node);

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
	public boolean rename(NodeDTO node, String newName);

	/**
	 * Je kategorie prázdná
	 * 
	 * @param node
	 * @return
	 */
	public boolean isEmpty(NodeDTO node);
}

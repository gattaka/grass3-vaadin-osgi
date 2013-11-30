package org.myftp.gattserver.grass3.facades;

import java.util.List;

import org.myftp.gattserver.grass3.model.dto.NodeDTO;

public interface INodeFacade {

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
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean createNewNode(NodeDTO parent, String name);

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
}

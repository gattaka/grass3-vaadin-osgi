package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;

public interface NodeFacade {

	/**
	 * Získá kategorii dle id
	 */
	public NodeOverviewDTO getNodeByIdForOverview(Long id);

	/**
	 * Získá kategorii dle id a namapuje jí, aby se dala použít v detailu
	 * kategorie
	 */
	public NodeDTO getNodeByIdForDetail(Long nodeId);

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeOverviewDTO> getRootNodes();

	/**
	 * Získá všechny kategorie pro zobrazení ve stromu
	 */
	public List<NodeOverviewDTO> getNodesForTree();

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeOverviewDTO> getNodesByParentNode(Long nodeId);

	/**
	 * Založí novou kategorii
	 * 
	 * @param parentId
	 *            pakliže je kategorii vkládána do jiné kategorie, je vyplněn id
	 *            předka. Pokud je kategorie vkládána přímo do kořene sekce, je
	 *            tento argument <code>null</code>
	 * @param name
	 *            jméno nové kategorie
	 * @return id kategorie pokud se přidání zdařilo, jinak <code>null</code>
	 */
	public Long createNewNode(Long parentId, String name);

	/**
	 * Přesune kategorii
	 * 
	 * @throws IllegalStateException
	 *             pokud zjistí, že je v grafu kategorií cykl a nejedná se tedy
	 *             o strom
	 * @throws IllegalArgumentException
	 *             pokud je vkládánaná kategorie předkem kategorie, do které je
	 *             vkládána -- nelze vložit předka do potomka
	 * @param nodeId
	 *            id kategorie k přesunu
	 * @param newParentId
	 *            id nového předka, do kterého má být kategorie přesunuta, nebo
	 *            <code>null</code> pokud má být přesunuta do kořene sekce
	 */
	public void moveNode(Long nodeId, Long newParentId);

	/**
	 * Smaže kategorii, pokud je prázdná
	 * 
	 * @param nodeId
	 *            id kategorie ke smazání
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public void deleteNode(Long nodeId);

	/**
	 * Přejmenuje kategorii
	 * 
	 * @param nodeId
	 *            id kategorie k přejmenování
	 * @param newName
	 *            nový název
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean rename(Long nodeId, String newName);

	/**
	 * Je kategorie prázdná?
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return
	 */
	public boolean isNodeEmpty(Long nodeId);
}

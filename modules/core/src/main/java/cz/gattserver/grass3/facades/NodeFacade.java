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
	 *            pakliže je kategorii vkládána do jiné kategorie, je vyplněn id
	 *            předka. Pokud je kategorie vkládána přímo do kořene sekce, je
	 *            tento argument <code>null</code>
	 * @param name
	 *            jméno nové kategorie
	 * @return id kategorie pokud se přidání zdařilo, jinak <code>null</code>
	 */
	public Long createNewNode(Long parent, String name);

	/**
	 * Přesune kategorii
	 * 
	 * @throws IllegalStateException
	 *             pokud zjistí, že je v grafu kategorií cykl a nejedná se tedy
	 *             o strom
	 * @throws IllegalArgumentException
	 *             pokud je vkládánaná kategorie předkem kategorie, do které je
	 *             vkládána -- nelze vložit předka do potomka
	 * @param node
	 *            id kategorie k přesunu
	 * @param newParent
	 *            id nového předka, do kterého má být kategorie přesunuta, nebo
	 *            <code>null</code> pokud má být přesunuta do kořene sekce
	 */
	public void moveNode(Long node, Long newParent);

	/**
	 * Smaže kategorii, pokud je prázdná
	 * 
	 * @param node
	 *            id kategorie ke smazání
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public void deleteNode(Long node);

	/**
	 * Přejmenuje kategorii
	 * 
	 * @param node
	 *            id kategorie k přejmenování
	 * @param newName
	 *            nový název
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean rename(Long node, String newName);

	/**
	 * Je kategorie prázdná?
	 * 
	 * @param node
	 *            id kategorie
	 * @return
	 */
	public boolean isEmpty(Long node);
}

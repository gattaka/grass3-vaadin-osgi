package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.interfaces.NodeTO;

public interface NodeFacade {

	/**
	 * Získá kategorii dle id
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return kategori dle id, namapovaná pro přehled
	 */
	public NodeOverviewTO getNodeByIdForOverview(Long nodeId);

	/**
	 * Získá kategorii dle id a namapuje jí, aby se dala použít v detailu
	 * kategorie
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return kategori dle id, namapovaná pro detail
	 */
	public NodeTO getNodeByIdForDetail(Long nodeId);

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeOverviewTO> getRootNodes();

	/**
	 * Získá všechny kategorie pro zobrazení ve stromu
	 */
	public List<NodeOverviewTO> getNodesForTree();

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeOverviewTO> getNodesByParentNode(Long nodeId);

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
	 */
	public void rename(Long nodeId, String newName);

	/**
	 * Je kategorie prázdná?
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return
	 */
	public boolean isNodeEmpty(Long nodeId);
}

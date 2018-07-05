package cz.gattserver.grass3.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.Node;

public interface ContentNodeService {

	/**
	 * Uloží {@link ContentNode} záznam o obsahu, který byl vytvořen nějakým
	 * modulem obsahů, a připojí ho ke {@link Node} kategorii dle parametru.
	 * 
	 * @param contentModuleId
	 *            identifikátor modulu obsahů
	 * @param contentId
	 *            id obsahu, který byl vytvořen modulem obsahů a ke kterému má
	 *            být vytvořen {@link ContentNode} záznam
	 * @param name
	 *            jméno obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            <code>true</code>, pokud je obsah publikován
	 * @param nodeId
	 *            id kategorie do které se vkládá
	 * @param author
	 *            id uživatele, který obsah vytvořil
	 * @param draft
	 *            <code>true</code>, pokud se jedná se o rozpracovaný obsah
	 * @param date
	 *            předdefinované datum vytvoření nebo <code>null</code>, pokud
	 *            je datem vytvoření aktuální čas
	 * @param draftSourceId
	 *            id existujícího zdrojového obsahu (vytvořeného modulem
	 *            obsahů), od kterého je draft nebo <code>null</code>
	 * @return id {@link ContentNode} záznamu, který byl k obsahu vytvořen
	 */
	public long save(String contentModuleId, long contentId, String name, Collection<String> tags, boolean publicated,
			long nodeId, long author, boolean draft, LocalDateTime date, Long draftSourceId);

	/**
	 * Získá contentNodeDTO dle jeho id
	 * 
	 * @param id
	 *            identifikátor obsahu
	 * @return obsah
	 */
	public ContentNodeTO getByID(long id);

	/**
	 * Upraví obsah a uloží ho do DB - verze metody pro obsah bez tagů
	 * 
	 * @param contentNodeId
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param name
	 *            název obsahu
	 * @param publicated
	 *            je obsah publikovaný
	 */
	public void modify(long contentNodeId, String name, boolean publicated);

	/**
	 * Upraví obsah a uloží ho do DB
	 * 
	 * @param contentNodeId
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param name
	 *            název obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            <code>true</code>, pokud je obsah publikován
	 */
	public void modify(long contentNodeId, String name, Collection<String> tags, boolean publicated);

	/**
	 * Upraví obsah a uloží ho do DB - verze s možností editace data vytvoření
	 * obsahu
	 * 
	 * @param contentNodeId
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param name
	 *            název obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            <code>true</code>, pokud je obsah publikován
	 * @param creationDate
	 *            vnucené datum vytvoření obsahu, které přepíše původní
	 *            automatické datum
	 */
	public void modify(long contentNodeId, String name, Collection<String> tags, boolean publicated,
			LocalDateTime creationDate);

	/**
	 * Smaže obsah dle id obecného uzlu obsahu
	 * 
	 * @param contentNodeId
	 *            id obecného uzlu obsahu
	 */
	public void deleteByContentNodeId(long contentNodeId);

	/**
	 * Smaže obsah dle id koncového obsahu
	 * 
	 * @param contentModuleId
	 *            identifikátor modulu obsahů
	 * @param contentId
	 *            id koncového obsahu v rámci modulu obsahů
	 */
	public void deleteByContentId(String contentModuleId, long contentId);

	/**
	 * Přesune obsah mezi kategoriemi
	 * 
	 * @param nodeId
	 *            id cílové kategorie
	 * @param contentNodeId
	 *            id obsahu k přesunutí
	 */
	public void moveContent(long nodeId, long contentNodeId);

	/**
	 * Získá počet všech obsahů (pro LazyQueryContainer)
	 * 
	 * @return počet všech obsahů v DB
	 */
	public int getCount();

	/**
	 * Získá stránku nedávno přidaných obsahů (pro LazyQueryContainer)
	 * 
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @return list nalezených obsahů dle stránkování
	 */
	public List<ContentNodeOverviewTO> getRecentAdded(int offset, int limit);

	/**
	 * Získá stránku nedávno upravených obsahů (pro LazyQueryContainer)
	 * 
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @return list nalezených obsahů dle stránkování
	 */
	public List<ContentNodeOverviewTO> getRecentModified(int offset, int limit);

	/**
	 * Získá počet obsahů dle tagu (pro LazyQueryContainer)
	 * 
	 * @param tagId
	 *            id tagu, dle kterého bude výběr omezen
	 * @return počet obsahů dle tagu
	 */
	public int getCountByTag(long tagId);

	/**
	 * Získá stránku obsahů dle tagu (pro LazyQueryContainer)
	 * 
	 * @param tagId
	 *            id tagu, dle kterého bude výběr omezen
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @return list nalezených obsahů dle stránkování
	 */
	public List<ContentNodeOverviewTO> getByTag(long tagId, int offset, int limit);

	/**
	 * Získá počet oblíbených obsahů dle uživatele (pro LazyQueryContainer)
	 * 
	 * @param userId
	 *            id uživatele, dle kterého bude výběr omezen
	 * @return počet obsahů dle uživatele
	 */
	public int getUserFavouriteCount(long userId);

	/**
	 * Získá stránku oblíbených obsahů dle uživatele (pro LazyQueryContainer)
	 * 
	 * @param userId
	 *            id uživatele, z jehož oblíbených budou obsahy čteny
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @return list nalezených obsahů dle stránkování a omezení
	 */
	public List<ContentNodeOverviewTO> getUserFavourite(long userId, int offset, int limit);

	/**
	 * Získá počet obsahů dle kategorie (pro LazyQueryContainer)
	 * 
	 * @param nodeId
	 *            id kategorie, dle kterého bude výběr omezen
	 * @return počet obsahů dle kategorie
	 */
	public int getCountByNode(long nodeId);

	/**
	 * Získá stránku obsahů dle kategorie (pro LazyQueryContainer)
	 * 
	 * @param nodeId
	 *            id kategorie, ve které budou obsahy hledány
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @return list nalezených obsahů dle stránkování a omezení
	 */
	public List<ContentNodeOverviewTO> getByNode(long nodeId, int offset, int limit);

}

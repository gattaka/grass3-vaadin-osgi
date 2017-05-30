package cz.gattserver.grass3.facades;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;

public interface IContentNodeFacade {

	/**
	 * Získá set oblíbených obsahů daného uživatele
	 */
	public List<ContentNodeOverviewDTO> getUserFavouriteContents(Long user);

	/**
	 * Získá set naposledy přidaných obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeOverviewDTO> getRecentAddedForOverview(int maxResults);

	/**
	 * Získá set naposledy upravených obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeOverviewDTO> getRecentModifiedForOverview(int maxResults);

	/**
	 * Uloží obsah do DB, uloží jeho contentNode a link na něj do Node -
	 * zkrácená verze metody pro obsah, jež nemá tagy
	 * 
	 * @param contentModule
	 *            identifikátor modulu obsahů
	 * @param contentNode
	 *            id obsahu (v rámci modulu), který je ukládán
	 * @param name
	 *            jméno obsahu
	 * @param publicated
	 *            je článek publikován ?
	 * @param node
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNodeDTO}, který byl k obsahu vytvořen,
	 *         nebo
	 */
	public ContentNode save(String contentModule, Long contentNode, String name, boolean publicated, Long node,
			Long author);

	/**
	 * Uloží obsah do DB, uloží jeho contentNode a link na něj do Node
	 * 
	 * @param contentModule
	 *            identifikátor modulu obsahů
	 * @param contentNode
	 *            id obsahu (v rámci modulu), který je ukládán
	 * @param name
	 *            jméno obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            je článek publikován ?
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNodeDTO}, který byl k obsahu vytvořen,
	 *         nebo
	 */
	public ContentNode save(String contentModule, Long contentNode, String name, Collection<String> tags,
			boolean publicated, Long nodeId, Long author);

	public ContentNode save(String contentModuleId, Long contentId, String name, Collection<String> tags,
			boolean publicated, Long nodeId, Long author, Date date);

	/**
	 * Získá contentNodeDTO dle jeho id
	 * 
	 * @param id
	 *            identifikátor obsahu
	 * @return obsah
	 */
	public ContentNodeDTO getByID(Long id);

	/**
	 * Upraví obsah a uloží ho do DB - verze metody pro obsah bez tagů
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 */
	public void modify(Long contentNode, String name, boolean publicated);

	/**
	 * Upraví obsah a uloží ho do DB
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            je článek publikován ?
	 */
	public void modify(Long contentNode, String name, Collection<String> tags, boolean publicated);

	public void modify(Long contentId, String name, Collection<String> tags, boolean publicated, Date creationDate);

	/**
	 * Smaže obsah
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 */
	public void delete(Long contentNode);

	/**
	 * Přesune obsah mezi kategoriemi
	 * 
	 * @param nodeDTO
	 *            cílová kategorie
	 * 
	 * @param contentNodeDTO
	 *            obsah
	 */
	public void moveContent(Long node, Long contentNode);

	/**
	 * Získá počet všech obsahů (pro LazyQueryContainer)
	 */
	public int getContentsCount();

	/**
	 * Získá stránku nedávno přidaných obsahů (pro LazyQueryContainer)
	 * 
	 * @param pageIndex
	 * @param count
	 */
	public List<ContentNodeOverviewDTO> getRecentAddedForOverview(int pageIndex, int count);

	/**
	 * Získá stránku nedávno upravených obsahů (pro LazyQueryContainer)
	 * 
	 * @param pageIndex
	 * @param count
	 */
	public List<ContentNodeOverviewDTO> getRecentModifiedForOverview(int pageIndex, int count);

}

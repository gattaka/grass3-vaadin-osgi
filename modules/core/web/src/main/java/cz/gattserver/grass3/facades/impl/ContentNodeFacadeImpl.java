package cz.gattserver.grass3.facades.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.facades.IContentTagFacade;
import cz.gattserver.grass3.facades.ISecurityFacade;
import cz.gattserver.grass3.facades.IUserFacade;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.dao.NodeRepository;
import cz.gattserver.grass3.model.dao.UserRepository;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.ContentNodeOverviewDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.model.util.Mapper;
import cz.gattserver.grass3.security.Role;

@Transactional
@Component("contentNodeFacade")
public class ContentNodeFacadeImpl implements IContentNodeFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Resource(name = "securityFacade")
	private ISecurityFacade securityFacade;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Autowired
	private NodeRepository nodeRepository;

	/**
	 * Získá set oblíbených obsahů daného uživatele
	 */
	public List<ContentNodeOverviewDTO> getUserFavourite(Long user) {
		User u = userRepository.findOne(user);
		if (u == null)
			return null;
		Set<ContentNode> contentNodes = u.getFavourites();

		if (contentNodes == null)
			return null;

		List<ContentNodeOverviewDTO> contentNodeDTOs = mapper.mapContentNodeOverviewCollection(contentNodes);

		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy přidaných obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeOverviewDTO> getRecentAddedForOverview(int maxResults) {
		List<ContentNode> contentNodes = contentNodeRepository
				.findByCreationDateNotNullOrderByCreationDateDesc(new PageRequest(0, maxResults)).getContent();
		List<ContentNodeOverviewDTO> contentNodeDTOs = mapper.mapContentNodeOverviewCollection(contentNodes);
		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy upravených obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeOverviewDTO> getRecentModifiedForOverview(int maxResults) {
		List<ContentNode> contentNodes = contentNodeRepository
				.findByLastModificationDateNotNullOrderByLastModificationDateDesc(new PageRequest(0, maxResults))
				.getContent();
		List<ContentNodeOverviewDTO> contentNodeDTOs = mapper.mapContentNodeOverviewCollection(contentNodes);
		return contentNodeDTOs;
	}

	/**
	 * Uloží obsah do DB, uloží jeho contentNode a link na něj do Node -
	 * zkrácená verze metody pro obsah, jež nemá tagy
	 * 
	 * @param contentModuleId
	 *            identifikátor modulu obsahů
	 * @param contentId
	 *            id obsahu (v rámci modulu), který je ukládán
	 * @param name
	 *            jméno obsahu
	 * @param publicated
	 *            je článek publikován ?
	 * @param node
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNode}, který byl k obsahu vytvořen, nebo
	 */
	public ContentNode save(String contentModuleId, Long contentId, String name, boolean publicated, Long node,
			Long author) {
		return save(contentModuleId, contentId, name, null, publicated, node, author);
	}

	public ContentNode save(String contentModuleId, Long contentId, String name, Collection<String> tags,
			boolean publicated, Long node, Long author) {
		return save(contentModuleId, contentId, name, tags, publicated, node, author, null);
	}

	/**
	 * Uloží obsah do DB, uloží jeho contentNode a link na něj do Node
	 * 
	 * @param contentModuleId
	 *            identifikátor modulu obsahů
	 * @param contentId
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
	public ContentNode save(String contentModuleId, Long contentId, String name, Collection<String> tags,
			boolean publicated, Long nodeId, Long author, Date date) {

		ContentNode contentNode = new ContentNode();
		contentNode.setContentId(contentId);
		contentNode.setContentReaderId(contentModuleId);
		contentNode.setCreationDate(date == null ? Calendar.getInstance().getTime() : date);
		contentNode.setName(name);
		contentNode.setPublicated(publicated);

		// Ulož contentNode
		Node parent = nodeRepository.findOne(nodeId);
		contentNode.setParent(parent);

		User user = userRepository.findOne(author);
		contentNode.setAuthor(user);

		contentNode = contentNodeRepository.save(contentNode);

		parent.getContentNodes().add(contentNode);
		parent = nodeRepository.save(parent);

		/**
		 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
		 * aktualizovat kvůli mazání tagů údaje v DB)
		 */
		contentTagFacade.saveTags(tags, contentNode);

		return contentNode;

	}

	/**
	 * Získá contentNodeDTO dle jeho id
	 * 
	 * @param contentNodeId
	 *            identifikátor obsahu
	 * @return obsah
	 */
	public ContentNodeDTO getByID(Long contentNodeId) {
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);
		ContentNodeDTO contentNodeDTO = mapper.mapContentNodeForDetail(contentNode);
		return contentNodeDTO;
	}

	/**
	 * Upraví obsah a uloží ho do DB - verze metody pro obsah bez tagů
	 * 
	 * @param contentNodeId
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public void modify(Long contentNodeId, String name, boolean publicated) {
		modify(contentNodeId, name, null, publicated);
	}

	/**
	 * Upraví obsah a uloží ho do DB
	 * 
	 * @param contentNodeId
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            je článek publikován ?
	 */
	public void modify(Long contentNodeId, String name, Collection<String> tags, boolean publicated) {
		modify(contentNodeId, name, tags, publicated, null);
	}

	public void modify(Long contentNodeId, String name, Collection<String> tags, boolean publicated,
			Date creationDate) {
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);

		contentNode.setLastModificationDate(Calendar.getInstance().getTime());
		contentNode.setName(name);
		contentNode.setPublicated(publicated);

		if (creationDate != null)
			contentNode.setCreationDate(creationDate);

		// Ulož změny v contentNode
		contentNodeRepository.save(contentNode);

		/**
		 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
		 * aktualizovat kvůli mazání tagů údaje v DB)
		 */
		contentTagFacade.saveTags(tags, contentNodeId);
	}

	/**
	 * Smaže obsah
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public void delete(Long contentNodeId) {
		userFacade.removeContentFromAllUsersFavourites(contentNodeId);

		// vymaž tagy
		contentTagFacade.saveTags(null, contentNodeId);

		// vymaž content node
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);

		Node node = contentNode.getParent();
		node.getContentNodes().remove(contentNode);
		node = nodeRepository.save(node);

		contentNodeRepository.delete(contentNode);
	}

	@Override
	public void moveContent(Long node, Long contentNodeId) {
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeId);
		Node newNode = nodeRepository.findOne(node);
		Node oldNode = nodeRepository.findOne(contentNode.getParent().getId());

		contentNode.setParent(newNode);
		contentNodeRepository.save(contentNode);

		newNode.getContentNodes().add(contentNode);
		nodeRepository.save(newNode);

		oldNode.getContentNodes().remove(contentNode);
		nodeRepository.save(oldNode);
	}

	/**
	 * Všechny obsahy
	 */

	private Page<ContentNode> innerByUserAccess(PageRequest pr) {
		UserInfoDTO user = securityFacade.getCurrentUser();
		return contentNodeRepository.findByUserAccess(user.getId(), user.getRoles().contains(Role.ADMIN), pr);
	}

	@Override
	public int getCount() {
		return (int) innerByUserAccess(new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewDTO> getRecentAdded(int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByUserAccess(new PageRequest(pageIndex, count, Sort.Direction.DESC, "creationDate")).getContent());
	}

	@Override
	public List<ContentNodeOverviewDTO> getRecentModified(int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByUserAccess(new PageRequest(pageIndex, count, Sort.Direction.DESC, "lastModificationDate"))
						.getContent());
	}

	/**
	 * Dle tagu
	 */

	private Page<ContentNode> innerByTagAndUserAccess(Long tagId, PageRequest pr) {
		UserInfoDTO user = securityFacade.getCurrentUser();
		return contentNodeRepository.findByTagAndUserAccess(tagId, user.getId(), user.getRoles().contains(Role.ADMIN),
				pr);
	}

	@Override
	public int getCountByTag(Long tagId) {
		return (int) innerByTagAndUserAccess(tagId, new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewDTO> getByTag(Long tagId, int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByTagAndUserAccess(tagId, new PageRequest(pageIndex, count, Sort.Direction.DESC, "creationDate"))
						.getContent());
	}

	/**
	 * Dle oblíbených uživatele
	 */

	private Page<ContentNode> innerByUserFavouritesAndUserAccess(Long userId, PageRequest pr) {
		UserInfoDTO user = securityFacade.getCurrentUser();
		return contentNodeRepository.findByUserFavouritesAndUserAccess(userId, user.getId(),
				user.getRoles().contains(Role.ADMIN), pr);
	}

	@Override
	public int getUserFavouriteCount(Long userId) {
		return (int) innerByUserFavouritesAndUserAccess(userId, new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewDTO> getUserFavourite(Long userId, int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByUserFavouritesAndUserAccess(userId, new PageRequest(pageIndex, count)).getContent());
	}

	/**
	 * Dle kategorie
	 */

	private Page<ContentNode> innerByNodeAndUserAccess(Long nodeId, PageRequest pr) {
		UserInfoDTO user = securityFacade.getCurrentUser();
		return contentNodeRepository.findByNodeAndUserAccess(nodeId, user.getId(), user.getRoles().contains(Role.ADMIN),
				pr);
	}

	@Override
	public int getCountByNode(Long nodeId) {
		return (int) innerByNodeAndUserAccess(nodeId, new PageRequest(1, 1)).getTotalElements();
	}

	@Override
	public List<ContentNodeOverviewDTO> getByNode(Long nodeId, int pageIndex, int count) {
		return mapper.mapContentNodeOverviewCollection(
				innerByNodeAndUserAccess(nodeId, new PageRequest(pageIndex, count)).getContent());
	}

}

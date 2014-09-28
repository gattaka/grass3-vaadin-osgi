package cz.gattserver.grass3.facades.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.facades.IContentTagFacade;
import cz.gattserver.grass3.facades.IUserFacade;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.dao.NodeRepository;
import cz.gattserver.grass3.model.dao.UserRepository;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.Node;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.util.Mapper;

@Transactional
@Component("contentNodeFacade")
public class ContentNodeFacadeImpl implements IContentNodeFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

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
	public List<ContentNodeDTO> getUserFavouriteContents(Long user) {
		User u = userRepository.findOne(user);
		if (u == null)
			return null;
		Set<ContentNode> contentNodes = u.getFavourites();

		if (contentNodes == null)
			return null;

		List<ContentNodeDTO> contentNodeDTOs = mapper.mapContentNodeCollection(contentNodes);

		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy přidaných obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeDTO> getRecentAddedForOverview(int maxResults) {
		List<ContentNode> contentNodes = contentNodeRepository.findByCreationDateNotNullOrderByCreationDateDesc(
				new PageRequest(0, maxResults)).getContent();
		List<ContentNodeDTO> contentNodeDTOs = mapper.mapContentNodesForRecentsOverview(contentNodes);
		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy upravených obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeDTO> getRecentModifiedForOverview(int maxResults) {
		List<ContentNode> contentNodes = contentNodeRepository
				.findByLastModificationDateNotNullOrderByLastModificationDateDesc(new PageRequest(0, maxResults))
				.getContent();
		List<ContentNodeDTO> contentNodeDTOs = mapper.mapContentNodesForRecentsOverview(contentNodes);
		return contentNodeDTOs;
	}

	/**
	 * Získá set obsahů dle kategorie
	 */
	public List<ContentNodeDTO> getContentNodesByNode(Long node) {
		Node n = nodeRepository.findOne(node);
		if (n == null)
			return null;

		List<ContentNodeDTO> contentNodeDTOs = mapper.mapContentNodeCollection(n.getContentNodes());
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
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNode}, který byl k obsahu vytvořen, nebo
	 */
	public ContentNode save(String contentModuleId, Long contentId, String name, boolean publicated, Long category,
			Long author) {
		return save(contentModuleId, contentId, name, null, publicated, category, author);
	}

	public ContentNode save(String contentModuleId, Long contentId, String name, Collection<String> tags,
			boolean publicated, Long category, Long author) {
		return save(contentModuleId, contentId, name, tags, publicated, category, author, null);
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
	 * @param category
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNodeDTO}, který byl k obsahu vytvořen,
	 *         nebo
	 */
	public ContentNode save(String contentModuleId, Long contentId, String name, Collection<String> tags,
			boolean publicated, Long category, Long author, Date date) {
		try {

			ContentNode contentNode = new ContentNode();
			contentNode.setContentId(contentId);
			contentNode.setContentReaderId(contentModuleId);
			contentNode.setCreationDate(date == null ? Calendar.getInstance().getTime() : date);
			contentNode.setName(name);
			contentNode.setPublicated(publicated);

			// Ulož contentNode
			Node parent = nodeRepository.findOne(category);
			if (parent == null)
				return null;
			contentNode.setParent(parent);

			User user = userRepository.findOne(author);
			if (user == null)
				return null;
			contentNode.setAuthor(user);

			contentNode = contentNodeRepository.save(contentNode);
			if (contentNode == null)
				return null;

			parent.getContentNodes().add(contentNode);
			parent = nodeRepository.save(parent);
			if (parent == null)
				return null;

			/**
			 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
			 * aktualizovat kvůli mazání tagů údaje v DB)
			 */
			if (contentTagFacade.saveTags(tags, contentId) == false)
				return null;

			return contentNode;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Získá contentNodeDTO dle jeho id
	 * 
	 * @param id
	 *            identifikátor obsahu
	 * @return obsah
	 */
	public ContentNodeDTO getByID(Long id) {
		ContentNode contentNode = contentNodeRepository.findOne(id);
		ContentNodeDTO contentNodeDTO = mapper.map(contentNode);
		return contentNodeDTO;
	}

	/**
	 * Upraví obsah a uloží ho do DB - verze metody pro obsah bez tagů
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public boolean modify(Long contentNode, String name, boolean publicated) {
		return modify(contentNode, name, null, publicated);
	}

	/**
	 * Upraví obsah a uloží ho do DB
	 * 
	 * @param contentId
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            je článek publikován ?
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public boolean modify(Long contentId, String name, Collection<String> tags, boolean publicated) {
		return modify(contentId, name, tags, publicated, null);
	}

	public boolean modify(Long contentId, String name, Collection<String> tags, boolean publicated, Date creationDate) {
		ContentNode contentNode = contentNodeRepository.findOne(contentId);

		contentNode.setLastModificationDate(Calendar.getInstance().getTime());
		contentNode.setName(name);
		contentNode.setPublicated(publicated);

		if (creationDate != null)
			contentNode.setCreationDate(creationDate);

		// Ulož změny v contentNode
		if (contentNodeRepository.save(contentNode) == null)
			return false;

		/**
		 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
		 * aktualizovat kvůli mazání tagů údaje v DB)
		 */
		if (contentTagFacade.saveTags(tags, contentId) == false)
			return false;

		return true;
	}

	/**
	 * Smaže obsah
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public boolean delete(Long contentId) {

		if (userFacade.removeContentFromAllUsersFavourites(contentId) == false)
			return false;

		// vymaž tagy
		if (contentTagFacade.saveTags(null, contentId) == false)
			return false;

		// vymaž content node
		ContentNode contentNode = contentNodeRepository.findOne(contentId);

		Node node = contentNode.getParent();
		node.getContentNodes().remove(contentNode);
		node = nodeRepository.save(node);
		if (node == null)
			return false;

		contentNodeRepository.delete(contentNode);
		return true;

	}

	@Override
	public void moveContent(Long node, Long contentId) {
		ContentNode contentNode = contentNodeRepository.findOne(contentId);
		Node newNode = nodeRepository.findOne(node);
		Node oldNode = nodeRepository.findOne(contentNode.getParent().getId());

		contentNode.setParent(newNode);
		contentNodeRepository.save(contentNode);

		newNode.getContentNodes().add(contentNode);
		nodeRepository.save(newNode);

		oldNode.getContentNodes().remove(contentNode);
		nodeRepository.save(oldNode);
	}

	@Override
	public int getContentsCount() {
		return (int) contentNodeRepository.count();
	}

	@Override
	public List<ContentNodeDTO> getRecentAddedForOverview(int pageIndex, int count) {
		return mapper.mapContentNodesForRecentsOverview(contentNodeRepository.findAll(
				new PageRequest(pageIndex, count, Sort.Direction.DESC, "creationDate")).getContent());
	}

	@Override
	public List<ContentNodeDTO> getRecentModifiedForOverview(int pageIndex, int count) {
		return mapper.mapContentNodesForRecentsOverview(contentNodeRepository.findAll(
				new PageRequest(pageIndex, count, Sort.Direction.DESC, "lastModificationDate")).getContent());
	}
}

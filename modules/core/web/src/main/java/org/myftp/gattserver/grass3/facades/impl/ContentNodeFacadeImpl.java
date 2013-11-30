package org.myftp.gattserver.grass3.facades.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentNodeFacade;
import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeRepository;
import org.myftp.gattserver.grass3.model.dao.NodeRepository;
import org.myftp.gattserver.grass3.model.dao.UserRepository;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.model.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	public Set<ContentNodeDTO> getUserFavouriteContents(UserInfoDTO userInfo) {
		User user = userRepository.findOne(userInfo.getId());
		Set<ContentNode> contentNodes = user.getFavourites();

		if (contentNodes == null)
			return null;

		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(contentNodes);

		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy přidaných obsahů
	 * 
	 * @param size
	 * @return
	 */
	public Set<ContentNodeDTO> getRecentAddedForOverview(int maxResults) {
		List<ContentNode> contentNodes = contentNodeRepository
				.findByCreationDateNotNullOrderByCreationDateDesc(
						new PageRequest(0, maxResults)).getContent();
		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodesForRecentsOverview(contentNodes);
		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy upravených obsahů
	 * 
	 * @param size
	 * @return
	 */
	public Set<ContentNodeDTO> getRecentModifiedForOverview(int maxResults) {
		List<ContentNode> contentNodes = contentNodeRepository
				.findByLastModificationDateNotNullOrderByLastModificationDateDesc(
						new PageRequest(0, maxResults)).getContent();
		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodesForRecentsOverview(contentNodes);
		return contentNodeDTOs;
	}

	/**
	 * Získá set obsahů dle kategorie
	 */
	public Set<ContentNodeDTO> getContentNodesByNode(NodeDTO nodeDTO) {
		Node node = nodeRepository.findOne(nodeDTO.getId());
		if (node == null)
			return null;

		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(node.getContentNodes());
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
	 * @return instanci {@link ContentNodeDTO}, který byl k obsahu vytvořen,
	 *         nebo
	 */
	public ContentNodeDTO save(String contentModuleId, Long contentId,
			String name, boolean publicated, NodeDTO category,
			UserInfoDTO author) {
		return save(contentModuleId, contentId, name, null, publicated,
				category, author);
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
	public ContentNodeDTO save(String contentModuleId, Long contentId,
			String name, Collection<String> tags, boolean publicated,
			NodeDTO category, UserInfoDTO author) {
		try {

			ContentNode contentNode = new ContentNode();
			contentNode.setContentId(contentId);
			contentNode.setContentReaderId(contentModuleId);
			contentNode.setCreationDate(Calendar.getInstance().getTime());
			contentNode.setName(name);
			contentNode.setPublicated(publicated);

			// Ulož contentNode
			Node parent = nodeRepository.findOne(category.getId());
			if (parent == null)
				return null;
			contentNode.setParent(parent);

			User user = userRepository.findOne(author.getId());
			if (user == null)
				return null;
			contentNode.setAuthor(user);

			ContentNode node = contentNodeRepository.save(contentNode);
			if (node == null)
				return null;

			parent.getContentNodes().add(contentNode);
			parent = nodeRepository.save(parent);
			if (parent == null)
				return null;

			/**
			 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
			 * aktualizovat kvůli mazání tagů údaje v DB)
			 */
			ContentNodeDTO contentNodeDTO = getByID(contentNode.getId());
			if (contentTagFacade.saveTags(tags, contentNodeDTO) == false)
				return null;

			return contentNodeDTO;

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
	public boolean modify(ContentNodeDTO contentNode, String name,
			boolean publicated) {
		return modify(contentNode, name, null, publicated);
	}

	/**
	 * Upraví obsah a uloží ho do DB
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @param publicated
	 *            je článek publikován ?
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public boolean modify(ContentNodeDTO contentNodeDTO, String name,
			Collection<String> tags, boolean publicated) {

		ContentNode contentNode = contentNodeRepository.findOne(contentNodeDTO
				.getId());

		contentNode.setLastModificationDate(Calendar.getInstance().getTime());
		contentNode.setName(name);
		contentNode.setPublicated(publicated);

		// Ulož změny v contentNode
		if (contentNodeRepository.save(contentNode) == null)
			return false;

		/**
		 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
		 * aktualizovat kvůli mazání tagů údaje v DB)
		 */
		if (contentTagFacade.saveTags(tags, contentNodeDTO) == false)
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
	public boolean delete(ContentNodeDTO contentNodeDTO) {

		if (userFacade.removeContentFromAllUsersFavourites(contentNodeDTO) == false)
			return false;

		// vymaž tagy
		if (contentTagFacade.saveTags(null, contentNodeDTO) == false)
			return false;

		// vymaž content node
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeDTO
				.getId());

		Node node = contentNode.getParent();
		node.getContentNodes().remove(contentNode);
		node = nodeRepository.save(node);
		if (node == null)
			return false;

		contentNodeRepository.delete(contentNode);
		return true;

	}
}

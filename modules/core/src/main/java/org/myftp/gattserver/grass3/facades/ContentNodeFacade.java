package org.myftp.gattserver.grass3.facades;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.model.dao.ContentNodeDAO;
import org.myftp.gattserver.grass3.model.dao.NodeDAO;
import org.myftp.gattserver.grass3.model.dao.UserDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum ContentNodeFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;

	/**
	 * Získá set oblíbených obsahů daného uživatele
	 */
	public Set<ContentNodeDTO> getUserFavouriteContents(UserInfoDTO userInfo) {
		UserDAO dao = new UserDAO();
		User user = dao.findByID(userInfo.getId());
		Set<ContentNode> contentNodes = user.getFavourites();

		if (contentNodes == null)
			return null;

		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(contentNodes);

		dao.closeSession();
		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy přidaných obsahů
	 * 
	 * @param size
	 * @return
	 */
	public Set<ContentNodeDTO> getRecentAdded(int maxResults) {
		ContentNodeDAO dao = new ContentNodeDAO();
		List<ContentNode> contentNodes = dao.findRecentAdded(maxResults);
		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(contentNodes);

		dao.closeSession();
		return contentNodeDTOs;
	}

	/**
	 * Získá set naposledy upravených obsahů
	 * 
	 * @param size
	 * @return
	 */
	public Set<ContentNodeDTO> getRecentModified(int maxResults) {
		ContentNodeDAO dao = new ContentNodeDAO();
		List<ContentNode> contentNodes = dao.findRecentEdited(maxResults);
		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(contentNodes);

		dao.closeSession();
		return contentNodeDTOs;
	}

	/**
	 * Získá set obsahů dle kategorie
	 */
	public Set<ContentNodeDTO> getContentNodesByNode(NodeDTO nodeDTO) {

		NodeDAO dao = new NodeDAO();

		Node node = dao.findByID(nodeDTO.getId());
		if (node == null) {
			dao.closeSession();
			return null;
		}

		Set<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(node.getContentNodes());
		dao.closeSession();

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
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNodeDTO}, který byl k obsahu vytvořen,
	 *         nebo
	 */
	public ContentNodeDTO save(String contentModuleId, Long contentId,
			String name, NodeDTO category, UserInfoDTO author) {
		return save(contentModuleId, contentId, name, null, category, author);
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
	 * @param category
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return instanci {@link ContentNodeDTO}, který byl k obsahu vytvořen,
	 *         nebo
	 */
	public ContentNodeDTO save(String contentModuleId, Long contentId,
			String name, String tags, NodeDTO category, UserInfoDTO author) {
		try {

			UserDAO userDAO = new UserDAO();
			User user = userDAO.findByID(author.getId());

			NodeDAO nodeDAO = new NodeDAO();
			Node node = nodeDAO.findByID(category.getId());

			ContentNode contentNode = new ContentNode();
			contentNode.setAuthor(user);
			contentNode.setContentId(contentId);
			contentNode.setContentReaderId(contentModuleId);
			contentNode.setCreationDate(Calendar.getInstance().getTime());
			contentNode.setName(name);
			contentNode.setParent(node);
			// TODO 
			//contentNode.setPublicated(publicated)

			Long contentNodeID = (Long) new ContentNodeDAO().save(contentNode);
			if (contentNodeID == null)
				return null;

			ContentNodeDTO contentNodeDTO = getByID(contentNodeID);

			/**
			 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
			 * aktualizovat kvůli mazání tagů údaje v DB)
			 */
			if (contentTagFacade.saveTags(tags == null ? "" : tags,
					contentNodeDTO) == false)
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

		ContentNodeDAO dao = new ContentNodeDAO();
		ContentNode contentNode = dao.findByID(id);

		ContentNodeDTO contentNodeDTO = mapper.map(contentNode);
		dao.closeSession();

		return contentNodeDTO;

	}

	/**
	 * Upraví obsah a uloží ho do DB - verze metody pro obsah bez tagů
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public boolean modify(ContentNodeDTO contentNode, String name) {
		return modify(contentNode, name, null);
	}

	/**
	 * Upraví obsah a uloží ho do DB
	 * 
	 * @param contentNode
	 *            uzel obsahu, který patří k tomuto obsahu
	 * @param tags
	 *            řetězec tagů, který se má společně s obsahem uložit
	 * @return true pokud proběhla úprava úspěšně jinak false
	 */
	public boolean modify(ContentNodeDTO contentNodeDTO, String name,
			String tags) {

		ContentNodeDAO contentNodeDAO = new ContentNodeDAO();
		ContentNode contentNode = contentNodeDAO.findByID(contentNodeDTO
				.getId());

		contentNode.setLastModificationDate(Calendar.getInstance().getTime());
		contentNode.setName(name);

		// Ulož změny v contentNode
		if (new ContentNodeDAO().merge(contentNode) == false)
			return false;

		/**
		 * Tagy - contentNode je uložen v rámce saveTags (musí se tam
		 * aktualizovat kvůli mazání tagů údaje v DB)
		 */
		if (contentTagFacade.saveTags(tags == null ? "" : tags, contentNodeDTO) == false)
			return false;

		return true;

	}

}

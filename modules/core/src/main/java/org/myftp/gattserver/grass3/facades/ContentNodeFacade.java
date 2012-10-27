package org.myftp.gattserver.grass3.facades;

import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.model.dao.ContentNodeDAO;
import org.myftp.gattserver.grass3.model.dao.UserDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum ContentNodeFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	/**
	 * Získá list oblíbených obsahů daného uživatele
	 */
	public List<ContentNodeDTO> getUserFavouritesContents(UserInfoDTO userInfo) {
		UserDAO dao = new UserDAO();
		User user = dao.findByID(userInfo.getId());
		Set<ContentNode> contentNodes = user.getFavourites();

		if (contentNodes == null)
			return null;

		List<ContentNodeDTO> contentNodeDTOs = mapper
				.mapContentNodeCollection(contentNodes);

		dao.closeSession();
		return contentNodeDTOs;
	}

	/**
	 * Získá list naposledy přidaných obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeDTO> getRecentAdded(int maxResults) {
		ContentNodeDAO dao = new ContentNodeDAO();
		List<ContentNode> contentNodes = dao.findRecentAdded(maxResults);
		List<ContentNodeDTO> contentNodeDTOs = mapper.mapContentNodeCollection(contentNodes);
		
		dao.closeSession();
		return contentNodeDTOs;
	}

	/**
	 * Získá list naposledy upravených obsahů
	 * 
	 * @param size
	 * @return
	 */
	public List<ContentNodeDTO> getRecentModified(int maxResults) {
		ContentNodeDAO dao = new ContentNodeDAO();
		List<ContentNode> contentNodes = dao.findRecentEdited(maxResults);
		List<ContentNodeDTO> contentNodeDTOs = mapper.mapContentNodeCollection(contentNodes);
		
		dao.closeSession();
		return contentNodeDTOs;
	}

}

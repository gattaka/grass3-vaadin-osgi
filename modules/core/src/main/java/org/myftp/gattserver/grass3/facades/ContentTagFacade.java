package org.myftp.gattserver.grass3.facades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.model.dao.ContentTagDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum ContentTagFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	public List<ContentTagDTO> getAllContentTags() {
		ContentTagDAO dao = new ContentTagDAO();

		List<ContentTag> contentTags = dao.findAll();
		if (contentTags == null)
			return null;
		List<ContentTagDTO> contentTagDTOs = mapper
				.mapContentTagCollection(contentTags);

		dao.closeSession();
		return contentTagDTOs;
	}

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tagNames
	 *            řetězec tagů oddělených mezerami
	 * @param contentNodeDTO
	 *            obsah, který je oanotován těmito tagy
	 * @return množina tagů, jako objektů, odpovídající těm ze vstupního řetězce
	 */
	public boolean saveTags(String tagNames, ContentNodeDTO contentNodeDTO) {

		// TODO regex ! jinak to bude dělat mezery součástí
		String[] tagArray = tagNames.split(",");

		// tag dao
		ContentTagDAO contentTagDAO = new ContentTagDAO();

		// tagy, které které jsou použity/vytvořeny
		Set<ContentTag> tags = new HashSet<ContentTag>();

		for (String tag : tagArray) {

			if (tag.isEmpty())
				continue;

			// existuje už takový tag ?
			ContentTag contentTag = contentTagDAO.findContentTagByName(tag);
			contentTagDAO.closeSession();

			if (contentTag == null) {
				// ne ? - vytvoř
				contentTag = new ContentTag();
				contentTag.setName(tag);
			}

			// přidej ho do seznamu
			tags.add(contentTag);

		}

		// proveď veškeré ukládání, nastavování a mazání nepotřebných tagů v
		// jedné transakci
		if (contentTagDAO.saveTagsOnContentNode(tags, contentNodeDTO.getId()) == false)
			return false;

		return true;
	}

	public ContentTagDTO getContentTagByName(String tagName) {

		ContentTagDAO dao = new ContentTagDAO();
		ContentTagDTO tag = mapper.map(dao.findContentTagByName(tagName));
		dao.closeSession();

		return tag;
	}

}

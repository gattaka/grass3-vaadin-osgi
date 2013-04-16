package org.myftp.gattserver.grass3.facades.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.model.dao.ContentTagDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.stereotype.Component;

@Component("contentTagFacade")
public class ContentTagFacadeImpl implements IContentTagFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Resource(name = "contentTagDAO")
	private ContentTagDAO contentTagDAO;

	// neměl by být tečka apod. znak, využívaný v regulárních výrazech
	public static final String TAGS_DELIMITER = ",";

	public List<ContentTagDTO> getContentTagsForOverview() {

		List<ContentTag> contentTags = contentTagDAO.findAll();
		if (contentTags == null)
			return null;
		List<ContentTagDTO> contentTagDTOs = mapper
				.mapContentTagCollectionForOverview(contentTags);

		contentTagDAO.closeSession();
		return contentTagDTOs;
	}

	public String[] parseTags(String tagNames) {

		return tagNames.split("(\\s*" + TAGS_DELIMITER
				+ "\\s*)|(^\\s+)|(\\s+$)");
	}

	public String serializeTags(Set<String> tags) {

		StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (String tag : tags) {
			if (!first) {
				// mezera pro přehlednost
				stringBuilder.append(TAGS_DELIMITER).append(" ");
			}
			first = false;
			stringBuilder.append(tag);
		}

		return stringBuilder.toString();

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

		// získej čisté tagy (bez oddělovacích znaků a mezer)
		String[] tagArray = parseTags(tagNames);

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

	/**
	 * Získej tag dle jeho jména
	 * 
	 * @param tagName
	 *            jméno tagu
	 * @return tag
	 */
	public ContentTagDTO getContentTagByName(String tagName) {

		ContentTagDTO tag = mapper.mapContentTag(contentTagDAO
				.findContentTagByName(tagName));
		contentTagDAO.closeSession();

		return tag;
	}

}

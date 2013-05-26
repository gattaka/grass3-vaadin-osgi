package org.myftp.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeRepository;
import org.myftp.gattserver.grass3.model.dao.ContentTagRepository;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("contentTagFacade")
public class ContentTagFacadeImpl implements IContentTagFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Autowired
	private ContentTagRepository contentTagRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	// neměl by být tečka apod. znak, využívaný v regulárních výrazech
	public static final String TAGS_DELIMITER = ",";

	public List<ContentTagDTO> getContentTagsForOverview() {
		List<ContentTag> contentTags = contentTagRepository.findAll();
		if (contentTags == null)
			return null;
		Set<ContentTagDTO> contentTagDTOs = mapper
				.mapContentTagCollectionForOverview(contentTags);
		return new ArrayList<ContentTagDTO>(contentTagDTOs);
	}

	public String[] parseTags(String tagNames) {
		return tagNames.split("(\\s*" + TAGS_DELIMITER
				+ "\\s*)|(^\\s+)|(\\s+$)");
	}

	public String serializeTags(Set<ContentTagDTO> tags) {

		StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (ContentTagDTO tag : tags) {
			if (!first) {
				// mezera pro přehlednost
				stringBuilder.append(TAGS_DELIMITER).append(" ");
			}
			first = false;
			stringBuilder.append(tag.getName());
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
			ContentTag contentTag = contentTagRepository.findByName(tag);

			if (contentTag == null) {
				// ne ? - vytvoř
				contentTag = new ContentTag();
				contentTag.setName(tag);
			}

			// přidej ho do seznamu
			tags.add(contentTag);

		}

		ContentNode contentNode = contentNodeRepository.findOne(contentNodeDTO
				.getId());
		if (contentNode == null)
			return false;

		// Fáze #1
		// získej tagy, které se už nepoužívají a na nich proveď odebrání
		// ContentNode a případně smazání
		Set<ContentTag> tagsToDelete = new HashSet<ContentTag>();
		for (ContentTag oldTag : contentNode.getContentTags()) {
			if (tags.contains(oldTag))
				continue;

			if (oldTag.getContentNodes().remove(contentNode) == false) {
				// TODO ... pokud nebyl node v tagu, pak je někde chyba a
				// měl by se aspon vyhodit warning
			}

			// ulož změnu
			oldTag.setContentNodesCount(oldTag.getContentNodes().size());
			oldTag = contentTagRepository.save(oldTag);
			if (oldTag == null)
				return false;

			// pokud je tag prázdný (nemá nodes) pak se může smazat
			if (oldTag.getContentNodes().isEmpty()) {
				tagsToDelete.add(oldTag);
			}
		}

		// Fáze #2
		// vymaž tagy z node
		// do všech tagů přidej odkaz na node
		// tagy ulož nebo na nich proveď merge
		// zároveň je rovnou přidej do node
		contentNode.getContentTags().clear();
		for (ContentTag tag : tags) {
			if (tag.getContentNodes() == null)
				tag.setContentNodes(new HashSet<ContentNode>());
			tag.getContentNodes().add(contentNode);

			// je nový ? Pak ho ulož a zkontroluj, že dostal id
			if (tag.getId() == null) {
				tag.setContentNodesCount(tag.getContentNodes().size());
				tag = contentTagRepository.save(tag);
				if (tag == null)
					return false;

			} else {
				tag.setContentNodesCount(tag.getContentNodes().size());
				tag = contentTagRepository.save(tag);
				if (tag == null)
					return false;
			}

			// přidej tag k node
			contentNode.getContentTags().add(tag);

		}

		// merge contentNode
		contentNode = contentNodeRepository.save(contentNode);
		if (contentNode == null)
			return false;

		// Fáze #3
		// smaž nepoužívané tagy
		for (ContentTag tagToDelete : tagsToDelete) {
			contentTagRepository.delete(tagToDelete);
		}

		return true;
	}

	/**
	 * Získej tag dle jeho id
	 * 
	 * @param id
	 * @return tag
	 */
	public ContentTagDTO getContentTagById(Long id) {
		ContentTagDTO tag = mapper.mapContentTag(contentTagRepository
				.findOne(id));
		return tag;
	}

	public boolean countContentNodes() {
		for (ContentTag tag : contentTagRepository.findAll()) {
			tag.setContentNodesCount(tag.getContentNodes().size());
			if (contentTagRepository.save(tag) == null)
				return false;
		}
		return true;
	}

}

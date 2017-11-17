package cz.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.dao.ContentTagRepository;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.dto.ContentTagCountTO;
import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;
import cz.gattserver.grass3.model.util.CoreMapper;

@Transactional
@Component
public class ContentTagFacadeImpl implements ContentTagFacade {

	@Autowired
	private CoreMapper mapper;

	@Autowired
	private ContentTagRepository contentTagRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Override
	public List<ContentTagOverviewDTO> getContentTagsForOverviewOrderedByName() {
		List<ContentTag> contentTags = contentTagRepository.findAll(new Sort("name"));
		Set<ContentTagOverviewDTO> contentTagDTOs = mapper.mapContentTagCollectionForOverview(contentTags);
		return new ArrayList<ContentTagOverviewDTO>(contentTagDTOs);
	}

	@Override
	public ContentTagOverviewDTO getContentTagById(Long id) {
		Validate.notNull(id, "Id hledaného tagu nemůže být null");
		return mapper.mapContentTag(contentTagRepository.findOne(id));
	}

	@Override
	public ContentTagOverviewDTO getContentTagByName(String name) {
		Validate.notNull(name, "Název hledaného tagu nemůže být null");
		return mapper.mapContentTag(contentTagRepository.findByName(name));
	}

	@Override
	public void saveTags(Collection<String> tagsDTOs, Long contentNodeId) {
		saveTags(tagsDTOs, contentNodeRepository.findOne(contentNodeId));
	}

	@Override
	public void saveTags(Collection<String> tagsDTOs, ContentNode contentNode) {

		// tagy, které které jsou použity/vytvořeny
		Set<ContentTag> tags = new HashSet<ContentTag>();

		if (tagsDTOs != null) {
			for (String tag : tagsDTOs) {
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
		}

		// Fáze #1
		// získej tagy, které se už nepoužívají a na nich proveď odebrání
		// ContentNode a případně smazání
		Set<ContentTag> tagsToDelete = new HashSet<ContentTag>();
		if (contentNode.getContentTags() != null) {
			for (ContentTag oldTag : contentNode.getContentTags()) {
				if (tags.contains(oldTag))
					continue;

				oldTag.getContentNodes().remove(contentNode);

				// ulož změnu
				oldTag = contentTagRepository.save(oldTag);

				// pokud je tag prázdný (nemá nodes) pak se může smazat
				if (oldTag.getContentNodes().isEmpty()) {
					tagsToDelete.add(oldTag);
				}
			}
		}

		// Fáze #3
		// smaž nepoužívané tagy
		for (ContentTag tagToDelete : tagsToDelete) {
			contentTagRepository.delete(tagToDelete);
		}
	}

	@Override
	public int getContentNodesCount(Long tagId) {
		return contentTagRepository.countContentTagContents(tagId);
	}

	@Override
	public Map<Long, Integer> getContentNodesCounts() {
		Map<Long, Integer> map = new LinkedHashMap<>();
		for (ContentTagCountTO to : contentTagRepository.countContentTagsContents())
			map.put(to.getId(), to.getContentsCount());
		return map;
	}

	@Override
	public ContentTagCountTO getTagContentNodesLowestCount() {
		return contentTagRepository.findTagContentNodesLowestCount();
	}

	@Override
	public ContentTagCountTO getTagContentNodesHighestCount() {
		return contentTagRepository.findTagContentNodesHighestCount();
	}

}

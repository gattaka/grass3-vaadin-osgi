package cz.gattserver.grass3.facades.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;

import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.dao.ContentTagRepository;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.domain.ContentTag;
import cz.gattserver.grass3.model.domain.QContentTag;
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
	public Set<ContentTagOverviewTO> getTagsForOverviewOrderedByName() {
		List<ContentTag> contentTags = contentTagRepository.findAll(new Sort(Direction.ASC, "name"));
		Set<ContentTagOverviewTO> contentTagDTOs = mapper.mapContentTagCollectionForOverview(contentTags);
		return contentTagDTOs;
	}

	@Override
	public ContentTagOverviewTO getTagById(Long id) {
		Validate.notNull(id, "Id hledaného tagu nemůže být null");
		return mapper.mapContentTag(contentTagRepository.findOne(id));
	}

	@Override
	public ContentTagOverviewTO getTagByName(String name) {
		Validate.notNull(name, "Název hledaného tagu nemůže být null");
		return mapper.mapContentTag(contentTagRepository.findByName(name));
	}

	@Override
	public void saveTags(Collection<String> tagsDTOs, Long contentNodeId) {
		saveTags(tagsDTOs, contentNodeRepository.findOne(contentNodeId));
	}

	@Override
	public void saveTags(Collection<String> tags, ContentNode contentNode) {
		// tagy, které které jsou použity/vytvořeny
		Set<ContentTag> tagsEntities = new HashSet<ContentTag>();
		if (tags != null) {
			for (String tag : tags) {
				// existuje už takový tag ?
				ContentTag contentTag = contentTagRepository.findByName(tag);
				if (contentTag == null) {
					contentTag = new ContentTag();
					contentTag.setName(tag);
					contentTagRepository.save(contentTag);
				}
				tagsEntities.add(contentTag);
			}
		}

		// Projdi ContentNode tagy a buď je ponech nebo je odeber, dle toho,
		// jaké tagy jsou teď k obsahu ukládané
		if (contentNode.getContentTags() != null) {
			for (ContentTag oldTag : contentNode.getContentTags()) {
				if (tags.contains(oldTag))
					continue;
				oldTag.getContentNodes().remove(contentNode);
			}
		}

		// Nahraď stávající kolekci tagů novou kolekcí
		contentNode.setContentTags(tagsEntities);
		contentNodeRepository.save(contentNode);

		// Vyčisti DB od nepoužívaných tagů
		contentTagRepository.deleteUnusedTags();
	}

	@Override
	public int getTagContentsCount(Long tagId) {
		Validate.notNull(tagId, "'tagId' nemůže být null");
		return contentTagRepository.countContentTagContents(tagId);
	}

	@Override
	public Map<Long, Integer> getTagsContentsCountsMap() {
		Map<Long, Integer> map = new LinkedHashMap<>();
		for (Tuple to : contentTagRepository.countContentTagsContents())
			map.put(to.get(QContentTag.contentTag.id), to.get(QContentTag.contentTag.contentNodes.size()));
		return map;
	}

	@Override
	public List<Integer> getTagsContentsCountsGroups() {
		List<Integer> list = new ArrayList<>();
		contentTagRepository.findContentNodesCountsGroups().forEach(i -> list.add(((BigInteger) i).intValue()));
		return list;
	}

	@Override
	public List<ContentTagsCloudItemTO> createTagsCloud(int maxFontSize, int minFontSize) {
		// Pro škálování je potřeba znát počty obsahů ze všech tagů
		Map<Long, Integer> countsMap = getTagsContentsCountsMap();
		if (countsMap.isEmpty())
			return new ArrayList<>();

		// Skupiny počtů -- je potřeba vědět, jaké součty existují, aby se dle
		// nich nastavily velikosti písma. Nemusí existovat všechny skupiny,
		// například žádný tag nemusí mít přesně 12 obsahů, takže je zbytečné
		// pro 12 počítat velikost, další velikostí v pořadí počtů může být
		// třeba až 17
		List<Integer> countsGroups = getTagsContentsCountsGroups();

		// Rozděl rozmezí velikosti fontů na tolik
		double scale = maxFontSize - minFontSize;
		int fontSizeStep = (int) Math.floor(scale / (countsGroups.size() == 1 ? 1 : (countsGroups.size() - 1)));
		if (fontSizeStep == 0)
			fontSizeStep = 1;

		// Údaj o poslední příčce a velikosti, která jí odpovídala
		int lastCountGroup = countsGroups.get(0);
		int lastFontSize = minFontSize;

		// Potřebuju aby bylo možné nějak zavolat svůj počet obsahů a zpátky se
		// vrátila velikost fontu, reps. kategorie velikosti.
		Map<Integer, Integer> fontSizeByCountsGroupMap = new HashMap<Integer, Integer>();
		for (Entry<Long, Integer> entry : countsMap.entrySet()) {
			// Spočítej jeho fontsize - pokud jsem vyšší, pak přihoď velikost
			// dle vypočteného přírůstku a ulož můj stav aby ostatní věděli,
			// jestli mají zvyšovat nebo zůstat, protože mají stejnou velikost
			int tagContentsCount = entry.getValue();
			if (tagContentsCount > lastCountGroup) {
				lastCountGroup = tagContentsCount;
				if (lastFontSize + fontSizeStep <= maxFontSize)
					lastFontSize += fontSizeStep;
			}

			int size = tagContentsCount;
			fontSizeByCountsGroupMap.put(size, lastFontSize);
		}

		List<ContentTagsCloudItemTO> itemslist = new ArrayList<>();

		// Vytáhni si tagy seřazené dle jména a dokonči vytváření datové sady
		// pro tags cloud
		Set<ContentTagOverviewTO> tags = getTagsForOverviewOrderedByName();
		for (ContentTagOverviewTO tag : tags) {
			ContentTagsCloudItemTO item = new ContentTagsCloudItemTO();
			item.setId(tag.getId());
			item.setContentsCount(countsMap.get(item.getId()));
			item.setFontSize(fontSizeByCountsGroupMap.get(item.getContentsCount()));
			item.setName(tag.getName());
			itemslist.add(item);
		}

		return itemslist;
	}

}

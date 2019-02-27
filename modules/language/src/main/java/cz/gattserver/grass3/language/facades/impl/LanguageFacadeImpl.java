package cz.gattserver.grass3.language.facades.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.data.provider.QuerySortOrder;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.dao.LanguageItemRepository;
import cz.gattserver.grass3.language.model.dao.LanguageRepository;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.domain.Language;
import cz.gattserver.grass3.language.model.domain.LanguageItem;
import cz.gattserver.grass3.language.model.dto.CrosswordTO;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.language.util.CrosswordBuilder;
import cz.gattserver.grass3.language.util.Mapper;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Transactional
@Component
public class LanguageFacadeImpl implements LanguageFacade {

	@Autowired
	private Mapper mapper;

	@Autowired
	private LanguageRepository langRepository;

	@Autowired
	private LanguageItemRepository itemRepository;

	/*
	 * Jazyky
	 */

	@Override
	public List<LanguageTO> getLanguages() {
		return mapper.mapLanguages(langRepository.findAll());
	}

	@Override
	public long saveLanguage(LanguageTO languageTO) {
		Language language = new Language();
		language.setId(languageTO.getId());
		language.setName(languageTO.getName());
		language = langRepository.save(language);
		return language.getId();
	}

	/*
	 * Záznamy
	 */

	@Override
	public int countLanguageItems(LanguageItemTO filterTO) {
		return (int) itemRepository.countAllByLanguage(filterTO);
	}

	@Override
	public List<LanguageItemTO> getLanguageItems(LanguageItemTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		List<LanguageItem> items = itemRepository.findAllByLanguageSortByName(filterTO,
				QuerydslUtil.transformOffsetLimit(offset, limit), QuerydslUtil.transformOrdering(sortOrder, s -> s));
		return mapper.mapLanguageItems(items);
	}

	@Override
	public LanguageItemTO getLanguageItemById(Long id) {
		return mapper.mapLanguageItem(itemRepository.findOne(id));
	}

	@Override
	public LanguageItemTO getLanguageItemByContent(long languageId, String content) {
		return mapper.mapLanguageItem(itemRepository.findLanguageItemByContent(languageId, content));
	}

	@Override
	public Long saveLanguageItem(LanguageItemTO itemTO) {
		LanguageItem item = mapper.mapLanguageItem(itemTO);
		item = itemRepository.save(item);
		return item.getId();
	}

	@Override
	public List<LanguageItemTO> getLanguageItemsForTest(long languageId, double minRating, double maxRatingExclusive,
			int maxCount, ItemType type) {
		List<Long> ids;
		if (type != null)
			ids = itemRepository.findIdsByLanguageAndSuccessRateRangeSortByContent(languageId, type, minRating,
					maxRatingExclusive);
		else
			ids = itemRepository.findIdsByLanguageAndSuccessRateRangeSortByContent(languageId, minRating,
					maxRatingExclusive);

		Set<Long> choosenIds = new HashSet<>();

		if (!ids.isEmpty())
			randomChoose(maxCount < ids.size() ? maxCount : ids.size(), ids, choosenIds);

		List<LanguageItem> items = itemRepository.findByIds(choosenIds);
		return mapper.mapLanguageItems(items);
	}

	private void randomChoose(int times, List<Long> ids, Set<Long> choosen) {
		if (!ids.isEmpty())
			IntStream.range(0, times).forEach(i -> {
				int rand = new Random().nextInt(ids.size());
				Long id = ids.get(rand);
				ids.remove(id);
				choosen.add(id);
			});
	}

	@Override
	public void updateItemAfterTest(LanguageItemTO item, boolean success) {
		int newCount = item.getTested() + 1;
		double newRate = (item.getSuccessRate() * item.getTested() + (success ? 1 : 0)) / newCount;
		itemRepository.updateItem(item.getId(), newCount, newRate, LocalDateTime.now());
	}

	@Override
	public void deleteLanguageItem(LanguageItemTO item) {
		itemRepository.delete(item.getId());
	}

	@Override
	public CrosswordTO prepareCrossword(LanguageItemTO filterTO, int size) {
		CrosswordBuilder crosswordBuilder = new CrosswordBuilder(size,
				itemRepository.findItemOfMaxLength(filterTO.getLanguage(), ItemType.WORD, size));
		return crosswordBuilder.build();
	}

	@Override
	public Float getSuccessRateOfLanguageAndType(ItemType type, Long langId) {
		return ((float) itemRepository.findSuccessRateSumByLanguageAndType(type, langId))
				/ itemRepository.countByLanguageAndType(type, langId);
	}

}

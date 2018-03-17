package cz.gattserver.grass3.language.facades.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gwt.user.client.Random;

import cz.gattserver.grass3.language.facades.LanguageFacade;
import cz.gattserver.grass3.language.model.dao.LanguageItemRepository;
import cz.gattserver.grass3.language.model.dao.LanguageRepository;
import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.domain.Language;
import cz.gattserver.grass3.language.model.domain.LanguageItem;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;
import cz.gattserver.grass3.language.util.Mapper;

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
	public List<LanguageItemTO> getLanguageItems(long languageId, ItemType type, int page, int size) {
		return mapper.mapLanguageItems(
				itemRepository.findAllByLanguageSortByName(languageId, type, new PageRequest(page, size)));
	}

	@Override
	public int countLanguageItems(long languageId, ItemType type) {
		return itemRepository.countAllByLanguage(languageId, type);
	}

	@Override
	public LanguageItemTO getLanguageItemById(Long id) {
		return mapper.mapLanguageItem(itemRepository.findOne(id));
	}

	@Override
	public Long saveLanguageItem(LanguageItemTO itemTO) {
		LanguageItem item = mapper.mapLanguageItem(itemTO);
		item = itemRepository.save(item);
		return item.getId();
	}

	@Override
	public List<LanguageItemTO> getLanguageItemsForTest(long languageId, ItemType type) {
		// sada na učení (ještě neumím)
		List<Long> toLearnIds = itemRepository.findIdsByLanguageAndSuccessRateRangeSortByContent(languageId, type, 0,
				5);
		// sada na vylepšení (umím trochu)
		List<Long> toImproveIds = itemRepository.findIdsByLanguageAndSuccessRateRangeSortByContent(languageId, type, 5,
				8);
		// sada na opakování (umím, stačí občas opakovat)
		// 1.1 je protože se max uvádí jako exclusive, tak aby se pobraly i ty,
		// co jsou případně opravdu 100% = 1
		List<Long> toCheckIds = itemRepository.findIdsByLanguageAndSuccessRateRangeSortByContent(languageId, type, 8,
				1.1);

		// Vybírám náhodně 10 věcí
		Set<Long> choosenIds = new HashSet<>();
		// 5 jich neumím
		randomChoose(5, toLearnIds, choosenIds);
		// 3 si potřebuju vylepšit
		randomChoose(3, toImproveIds, choosenIds);
		// 2 si opakuju, abych nezapomněl
		randomChoose(2, toCheckIds, choosenIds);

		List<LanguageItem> items = itemRepository.findByIds(choosenIds);
		return mapper.mapLanguageItems(items);
	}

	private void randomChoose(int times, List<Long> ids, Set<Long> choosen) {
		if (!ids.isEmpty())
			IntStream.range(0, times).forEach(i -> choosen.add(ids.get(Random.nextInt(ids.size()))));
	}

	@Override
	public void updateItemAfterTest(LanguageItemTO item, boolean success) {
		int newCount = item.getTested() + 1;
		double newRate = (item.getSuccessRate() * item.getTested() + (success ? 1 : 0)) / newCount;
		itemRepository.updateItem(item.getId(), newCount, newRate, LocalDateTime.now());
	}

}

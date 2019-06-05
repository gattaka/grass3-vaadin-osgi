package cz.gattserver.grass3.language.model.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.language.model.domain.LanguageItem;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;

public interface LanguageItemRepositoryCustom {

	long countAllByLanguage(LanguageItemTO filterTO);

	List<LanguageItem> findAllByLanguageSortByName(LanguageItemTO filterTO, PageRequest pageable,
			OrderSpecifier<?>[] order);

}

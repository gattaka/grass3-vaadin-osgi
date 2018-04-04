package cz.gattserver.grass3.language.model.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.language.model.dao.LanguageItemRepositoryCustom;
import cz.gattserver.grass3.language.model.domain.LanguageItem;
import cz.gattserver.grass3.language.model.domain.QLanguageItem;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Repository
public class LanguageItemRepositoryCustomImpl implements LanguageItemRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateHWItems(LanguageItemTO filterTO) {
		QLanguageItem l = QLanguageItem.languageItem;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(l.language.id, filterTO.getLanguage());
		builder.eq(l.type, filterTO.getType());
		builder.like(l.content, filterTO.getContent());
		builder.like(l.translation, filterTO.getTranslation());
		return builder.getBuilder();
	}

	@Override
	public long countAllByLanguage(LanguageItemTO filterTO) {
		JPAQuery<LanguageItem> query = new JPAQuery<>(entityManager);
		QLanguageItem l = QLanguageItem.languageItem;
		return query.from(l).where(createPredicateHWItems(filterTO)).fetchCount();
	}

	@Override
	public List<LanguageItem> findAllByLanguageSortByName(LanguageItemTO filterTO, PageRequest pageable,
			OrderSpecifier<?>[] order) {
		JPAQuery<LanguageItem> query = new JPAQuery<>(entityManager);
		QuerydslUtil.applyPagination(pageable, query);
		QLanguageItem l = QLanguageItem.languageItem;
		return query.from(l).where(createPredicateHWItems(filterTO)).orderBy(order).fetch();
	}

}

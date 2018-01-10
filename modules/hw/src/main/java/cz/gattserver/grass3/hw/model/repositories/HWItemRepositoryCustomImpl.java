package cz.gattserver.grass3.hw.model.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.hw.interfaces.HWFilterTO;
import cz.gattserver.grass3.hw.model.domain.HWItem;
import cz.gattserver.grass3.hw.model.domain.HWItemType;
import cz.gattserver.grass3.hw.model.domain.QHWItem;
import cz.gattserver.grass3.hw.model.domain.QHWItemType;
import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Repository
public class HWItemRepositoryCustomImpl implements HWItemRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateHWItems(HWFilterTO filter) {
		QHWItem h = QHWItem.hWItem;
		QHWItemType t = QHWItemType.hWItemType;
		PredicateBuilder builder = new PredicateBuilder();
		builder.like(h.name, filter.getName());
		builder.eq(h.state, filter.getState());
		builder.like(h.usedIn.name, filter.getUsedIn());
		builder.like(h.supervizedFor, filter.getSupervizedFor());
		builder.eq(h.price, filter.getPrice());
		builder.between(h.purchaseDate, filter.getPurchaseDateFrom(), filter.getPurchaseDateTo());
		if (filter.getTypes() != null)
			for (String type : filter.getTypes()) {
				JPAQuery<HWItemType> subQuery = new JPAQuery<>();
				subQuery.from(t).where(t.name.eq(type), h.types.contains(t));
				builder.exists(subQuery);
			}
		return builder.getBuilder();
	}

	@Override
	public long countHWItems(HWFilterTO filter) {
		JPAQuery<HWItem> query = new JPAQuery<>(entityManager);
		QHWItem h = QHWItem.hWItem;
		return query.from(h).where(createPredicateHWItems(filter)).fetchCount();
	}

	@Override
	public List<HWItem> getHWItems(HWFilterTO filter, Pageable pageable, OrderSpecifier<?>[] order) {
		JPAQuery<HWItem> query = new JPAQuery<>(entityManager);
		QuerydslUtil.applyPagination(pageable, query);
		QHWItem h = QHWItem.hWItem;
		return query.from(h).where(createPredicateHWItems(filter)).orderBy(order).fetch();
	}

}

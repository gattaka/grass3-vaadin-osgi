package cz.gattserver.grass3.hw.model.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

@Repository
public class HWItemRepositoryCustomImpl implements HWItemRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateHWItems(HWFilterTO filter) {
		QHWItem h = QHWItem.hWItem;
		QHWItemType t = QHWItemType.hWItemType;
		PredicateBuilder builder = new PredicateBuilder();
		builder.anyILike(h.name, filter.getName());
		builder.eq(h.state, filter.getState());
		builder.iLike(h.usedIn.name, filter.getUsedIn());
		builder.iLike(h.supervizedFor, filter.getSupervizedFor());
		builder.eq(h.price, filter.getPrice());
		builder.between(h.purchaseDate, filter.getPurchaseDateFrom(), filter.getPurchaseDateTo());
		builder.ne(h.id, filter.getIgnoreId());
		if (Boolean.TRUE == filter.getPublicItem())
			builder.eq(h.publicItem, true);
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
	public List<HWItem> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<HWItem> query = new JPAQuery<>(entityManager);
		QHWItem h = QHWItem.hWItem;
		query.offset(offset).limit(limit);
		return query.from(h).where(createPredicateHWItems(filter)).orderBy(order).fetch();
	}

}

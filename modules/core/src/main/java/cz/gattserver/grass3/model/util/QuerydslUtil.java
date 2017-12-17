package cz.gattserver.grass3.model.util;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;

public final class QuerydslUtil {

	public interface SortColumnReplacer {
		String replace(String column);
	}

	private QuerydslUtil() {
	}

	public static <T> JPAQuery<T> applyPagination(Pageable pageable, JPAQuery<T> query) {
		if (pageable == null) {
			return query;
		}
		query.offset(pageable.getOffset());
		query.limit(pageable.getPageSize());
		return query;
	}

	public static OrderSpecifier<String>[] transformOrdering(Object[] sortPropertyIds, boolean[] asc) {
		@SuppressWarnings("unchecked")
		OrderSpecifier<String>[] specifiers = new OrderSpecifier[sortPropertyIds.length];
		for (int i = 0; i < sortPropertyIds.length; i++) {
			specifiers[i] = new OrderSpecifier<>(asc[i] ? Order.ASC : Order.DESC,
					ExpressionUtils.path(String.class, (String) sortPropertyIds[i]));
		}
		return specifiers;
	}

	public static OrderSpecifier<String>[] transformOrdering(List<QuerySortOrder> sortProperties,
			SortColumnReplacer columnReplacer) {
		@SuppressWarnings("unchecked")
		OrderSpecifier<String>[] specifiers = new OrderSpecifier[sortProperties.size()];
		for (int i = 0; i < sortProperties.size(); i++) {
			specifiers[i] = new OrderSpecifier<>(
					sortProperties.get(i).getDirection().equals(SortDirection.ASCENDING) ? Order.ASC : Order.DESC,
					ExpressionUtils.path(String.class, columnReplacer.replace(sortProperties.get(i).getSorted())));
		}
		return specifiers;
	}

}

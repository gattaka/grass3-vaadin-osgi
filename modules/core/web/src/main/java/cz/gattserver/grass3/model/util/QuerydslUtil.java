package cz.gattserver.grass3.model.util;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

public final class QuerydslUtil {

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
			specifiers[i] = new OrderSpecifier<String>(asc[i] ? Order.ASC : Order.DESC,
					ExpressionUtils.path(String.class, (String) sortPropertyIds[i]));
		}
		return specifiers;
	}

}

package cz.gattserver.grass3.model.util;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

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

	/**
	 * Převádí offset a limit na page a pagesize
	 * 
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @param dir
	 *            směr řazení
	 * @param prop
	 *            property podle které se bude řadit
	 * @return {@link PageRequest} objekt
	 */
	public static PageRequest transformOffsetLimit(int offset, int limit, Direction dir, String prop) {
		// zjisti, zda offset je celočíselný násobek limitu
		int mod = offset % limit;

		int page;
		int pagesize;

		// pokud ano, pak je možné je přímo přepočítat na page a pagesize
		if (mod == 0) {
			page = offset / limit;
			pagesize = limit;
		} else {
			// pokud ne, pak limit je nějaký zbytek, například pro případ jako
			// offset = 40 a limit = 16 není možné převádět na page a pagesize:
			// 40/16 = 2,5 -> 2 (musí být celé číslo)
			// page = 2 a pagesize = 16 udělá že se přeskočí prvních 32 záznamů
			// a pak se nahraje dalších 16, tedy záznamy <33-48>, což je ale
			// o 8 méně, než by se mělo vzít dle offset a limit <41-56>
			// Zvětšit pagesize nemůžu, protože by to přestalo fungovat s page

			// V takovém případ se ale jedná evidentně o zbytek, takže je rovnou
			// možné stávající offset brát jako pagesize a page pak nastavit
			// jako 1, aby se přeskočila 1x pagesize (offset) a poté se nahrál
			// limit <= pagesize; limit může být větší než skutečně zbývá dat
			page = 1;
			pagesize = offset;
		}

		if (dir != null && prop != null)
			return new PageRequest(page, pagesize, dir, prop);
		else
			return new PageRequest(page, pagesize);
	}

	/**
	 * Převádí offset a limit na page a pagesize
	 * 
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @return {@link PageRequest} objekt
	 */
	public static PageRequest transformOffsetLimit(int offset, int limit) {
		return transformOffsetLimit(offset, limit, null, null);
	}

	/**
	 * Aplikuje vlastnosti stránkování
	 * 
	 * @param <T>
	 *            typ {@link JPAQuery}
	 * @param pageable
	 *            definice stránkování
	 * @param query
	 *            pro aplikaci stránkování
	 * @return {@link PageRequest} objekt
	 */
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

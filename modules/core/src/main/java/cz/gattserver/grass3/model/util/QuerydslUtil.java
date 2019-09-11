package cz.gattserver.grass3.model.util;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

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
			return PageRequest.of(page, pagesize, dir, prop);
		else
			return PageRequest.of(page, pagesize);
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
	 * Upravuje jednoduchý filtrační text na like výraz pro JPA, aby se dal
	 * použít přímo v LIKE výrazu, zatímco vstupem může být cokoliv, včetně
	 * <code>null</code> hodnoty. Pokud je <code>null</code>, udělá z něj
	 * prázdný řetězec s % znakem pro LIKE, jinak nahrazuje * znaky znakem % a
	 * nakonec ještě jeden přidává.
	 * 
	 * @param filter
	 * @return upravený řetězec pro LIKE filtr
	 */
	public static String transformSimpleLikeFilter(String filter) {
		return filter == null ? "%" : filter.replace('*', '%').concat("%");
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
		if (pageable.getSort() != null)
			pageable.getSort().forEach(s -> query.orderBy(transformOrder(s.getDirection(), s.getProperty())));
		return query;
	}

	public static OrderSpecifier<String> transformOrder(boolean ascending, String property) {
		return new OrderSpecifier<>(ascending ? Order.ASC : Order.DESC, ExpressionUtils.path(String.class, property));
	}

	public static OrderSpecifier<String> transformOrder(Direction direction, String property) {
		return new OrderSpecifier<>(Direction.ASC.equals(direction) ? Order.ASC : Order.DESC,
				ExpressionUtils.path(String.class, property));
	}

	public static OrderSpecifier<String> transformOrder(SortDirection sortDirection, String property) {
		return new OrderSpecifier<>(SortDirection.ASCENDING.equals(sortDirection) ? Order.ASC : Order.DESC,
				ExpressionUtils.path(String.class, property));
	}

	public static OrderSpecifier<String> transformOrder(Order order, String property) {
		return new OrderSpecifier<>(order, ExpressionUtils.path(String.class, property));
	}

	public static OrderSpecifier<String>[] transformOrdering(Object[] sortPropertyIds, boolean[] asc) {
		@SuppressWarnings("unchecked")
		OrderSpecifier<String>[] specifiers = new OrderSpecifier[sortPropertyIds.length];
		for (int i = 0; i < sortPropertyIds.length; i++)
			specifiers[i] = transformOrder(asc[i], (String) sortPropertyIds[i]);
		return specifiers;
	}

	public static OrderSpecifier<String>[] transformOrdering(List<QuerySortOrder> sortProperties,
			SortColumnReplacer columnReplacer) {
		@SuppressWarnings("unchecked")
		OrderSpecifier<String>[] specifiers = new OrderSpecifier[sortProperties.size()];
		for (int i = 0; i < sortProperties.size(); i++)
			specifiers[i] = transformOrder(sortProperties.get(i).getDirection(),
					columnReplacer.replace(sortProperties.get(i).getSorted()));
		return specifiers;
	}

}

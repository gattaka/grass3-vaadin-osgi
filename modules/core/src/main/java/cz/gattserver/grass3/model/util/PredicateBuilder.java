package cz.gattserver.grass3.model.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Date;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.common.util.DateUtil;

public class PredicateBuilder {

	private BooleanBuilder booleanBuilder;

	public PredicateBuilder() {
		this.booleanBuilder = new BooleanBuilder();
	}

	/**
	 * Přidání porovnání je rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder eq(StringPath path, String value) {
		if (isNotBlank(value)) {
			booleanBuilder.and(path.eq(value));
		}
		return this;
	}

	public PredicateBuilder eq(BooleanPath path, boolean value) {
		booleanBuilder.and(path.eq(value));
		return this;
	}

	public <T extends Enum<T>> PredicateBuilder eq(EnumPath<T> path, T value) {
		if (value != null)
			booleanBuilder.and(path.eq(value));
		return this;
	}

	public PredicateBuilder eqSpace(StringPath path) {
		booleanBuilder.and(path.isNull().or(path.isEmpty()).or(path.eq(" ")));
		return this;
	}

	public PredicateBuilder eqZero(NumberExpression<Long> path) {
		booleanBuilder.and(path.isNull().or(path.eq(0L)));
		return this;
	}

	/**
	 * Přidání porvnání je rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder eq(NumberExpression<T> path, T value) {
		if (value != null) {
			booleanBuilder.and(path.eq(value));
		}
		return this;
	}

	/**
	 * Přidání porvnání je není rovno rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder ne(NumberExpression<T> path, T value) {
		if (value != null) {
			booleanBuilder.and(path.ne(value));
		}
		return this;
	}

	/**
	 * Přidání porvnání není rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder ne(StringPath path, String value) {
		if (value != null) {
			booleanBuilder.and(ExpressionUtils.or(path.isNull(), path.ne(value)));
		}
		return this;
	}

	public PredicateBuilder ne(StringExpression expression, String value) {
		if (value != null) {
			booleanBuilder.and(ExpressionUtils.or(expression.isNull(), expression.ne(value)));
		}
		return this;
	}

	public PredicateBuilder neEmpty(StringPath path) {
		booleanBuilder.and(path.isNotNull());
		booleanBuilder.and(path.isNotEmpty());
		booleanBuilder.and(path.ne(" "));
		booleanBuilder.and(path.ne("0"));
		return this;
	}

	public <T extends Number & Comparable<?>> PredicateBuilder gtOrValueNull(NumberExpression<T> path,
			NumberExpression<T> value) {
		if (value != null) {
			// je potřeba pro případy porovnání x > null, pak je výsledek totiž
			// false, s porovnáním na "nebo null" pak takové případy vychází
			booleanBuilder.and(ExpressionUtils.or(path.gt(value), value.isNull()));
		}
		return this;
	}

	public <T extends Number & Comparable<?>> PredicateBuilder gt(NumberExpression<T> path, T value) {
		if (value != null) {
			booleanBuilder.and(path.gt(value));
		}
		return this;
	}

	public <T extends Number & Comparable<?>> PredicateBuilder ge(NumberExpression<T> path, T value) {
		if (value != null) {
			booleanBuilder.and(path.goe(value));
		}
		return this;
	}

	public <T extends Number & Comparable<?>> PredicateBuilder lt(NumberExpression<T> path, T value) {
		if (value != null) {
			booleanBuilder.and(path.lt(value));
		}
		return this;
	}

	public <T extends Number & Comparable<?>> PredicateBuilder le(NumberExpression<T> path, T value) {
		if (value != null) {
			booleanBuilder.and(path.loe(value));
		}
		return this;
	}

	/**
	 * Přidání porovnání NOT EXISTS.
	 * 
	 * @param query
	 *            sub query
	 * @return this pro řetězení
	 */
	public PredicateBuilder notExists(JPAQuery<?> query) {
		if (query != null) {
			booleanBuilder.and(query.notExists());
		}
		return this;
	}

	/**
	 * Přidání porvnání EXISTS.
	 * 
	 * @param query
	 *            sub query
	 * @return this pro řetězení
	 */
	public PredicateBuilder exists(JPAQuery<?> query) {
		if (query != null) {
			booleanBuilder.and(query.exists());
		}
		return this;
	}

	/**
	 * Přidání porvnání NOT IN.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param values
	 *            hodnoty pro porovnání
	 * 
	 * @return this pro řetězení
	 */
	public PredicateBuilder notIn(StringPath path, String[] values) {
		if (values != null) {
			booleanBuilder.and(path.notIn(values));
		}
		return this;
	}

	/**
	 * Přidání porvnání IN.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param values
	 *            hodnoty pro porovnání
	 * 
	 * @return this pro řetězení
	 */
	public PredicateBuilder in(StringPath path, String[] values) {
		if (values != null) {
			booleanBuilder.and(path.in(values));
		}
		return this;
	}

	/**
	 * Přidání porvnání IS NULL.
	 * 
	 * @param expression
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder isNull(NumberExpression<T> expression) {
		if (expression != null) {
			booleanBuilder.and(expression.isNull());
		}
		return this;
	}

	public PredicateBuilder isNull(BeanPath<?> expression) {
		if (expression != null) {
			booleanBuilder.and(expression.isNull());
		}
		return this;
	}

	/**
	 * Přidání porvnání IS NULL nebo je ' '.
	 * 
	 * @param expression
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public PredicateBuilder isEmpty(StringExpression expression) {
		if (expression != null) {
			booleanBuilder.and(
					ExpressionUtils.or(expression.eq(""), ExpressionUtils.or(expression.isNull(), expression.eq(" "))));
		}
		return this;
	}

	/**
	 * Přidání porvnání IS NOT NULL a zároveň není ' '.
	 * 
	 * @param expression
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public PredicateBuilder isNotEmpty(StringExpression expression) {
		if (expression != null) {
			booleanBuilder.and(expression.isNotNull());
			booleanBuilder.and(expression.ne(" "));
			booleanBuilder.and(expression.ne(""));
		}
		return this;
	}

	/**
	 * Přidání porvnání IS NOT NULL.
	 * 
	 * @param expression
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public PredicateBuilder isNotNull(NumberExpression<?> expression) {
		if (expression != null) {
			booleanBuilder.and(expression.isNotNull());
		}
		return this;
	}

	public PredicateBuilder isNotNull(BeanPath<?> expression) {
		if (expression != null) {
			booleanBuilder.and(expression.isNotNull());
		}
		return this;
	}

	private String prepareForLike(String value) {
		// nahradí znaky * znakem % pro SQL a JPQL vyhledávání v LIKE a navíc
		// přidá ještě jednou % aby se smazal rozdíl mezi údaji v DB, které mají
		// za sebou mezery a údaji v aplikaci, které se zadávají bez mezer
		return value.replace('*', '%') + '%';
	}

	/**
	 * Přidá porovnání SQL like.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder like(StringExpression path, String value) {
		if (isNotBlank(value) && !"*".equals(value)) {
			booleanBuilder.and(path.likeIgnoreCase(prepareForLike(value)));
		}
		return this;
	}

	public PredicateBuilder notLike(StringExpression path, String value) {
		if (isNotBlank(value)) {
			booleanBuilder.andNot(path.likeIgnoreCase(prepareForLike(value)));
		}
		return this;
	}

	/**
	 * Přidá porovnání SQL like.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder like(NumberExpression<?> path, String value) {
		if (isNotBlank(value)) {
			booleanBuilder.and(path.like(prepareForLike(value)));
		}
		return this;
	}

	public PredicateBuilder like(NumberPath<Long> path, Long value) {
		if (value != null) {
			like(path, value.toString());
		}
		return this;

	}

	/**
	 * Přidá porovnání SQL between.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param from
	 *            hodnota pro porovnání
	 * @param to
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder between(StringPath path, String from, String to) {
		if (isNotBlank(from) && isNotBlank(to)) {
			booleanBuilder.and(path.between(from, to));
		} else if (isNotBlank(from)) {
			like(path, prepareForLike(from));
		} else if (isNotBlank(to)) {
			like(path, prepareForLike(to));
		}
		return this;
	}

	/**
	 * Přidá porovnání SQL between.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param from
	 *            hodnota pro porovnání
	 * @param to
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder between(NumberExpression<T> path, T from, T to) {
		if (from != null && to != null) {
			booleanBuilder.and(path.between(from, to));
		} else if (from != null) {
			booleanBuilder.and(path.eq(from));
		} else if (to != null) {
			booleanBuilder.and(path.eq(to));
		}
		return this;
	}

	/**
	 * Přidá porovnání SQL between.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param from
	 *            hodnota pro porovnání
	 * @param to
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder between(DateTimePath<Date> path, Date from, Date to) {
		if ((from != null) && to != null) {
			booleanBuilder.and(path.between(DateUtil.resetTime(from), DateUtil.resetTimeToMidnight(to)));
		} else if (from != null) {
			booleanBuilder.and(path.gt(DateUtil.resetTime(from)));
		} else if (to != null) {
			booleanBuilder.and(path.lt(DateUtil.resetTime(to)));
		}
		return this;
	}

	/**
	 * Vrací celkový objekt predicate pro použítí v dotazu.
	 */
	public BooleanBuilder getBuilder() {
		return booleanBuilder;
	}

}

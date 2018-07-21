package cz.gattserver.grass3.drinks.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.domain.QBeerInfo;
import cz.gattserver.grass3.drinks.model.domain.QDrink;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.QBeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.QBeerTO;
import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Repository
public class DrinkRepositoryCustomImpl implements DrinkRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateBeers(BeerOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.BEER);
		builder.iLike(b.brewery, filterTO.getBrewery());
		builder.iLike(d.name, filterTO.getName());
		builder.iLike(b.category, filterTO.getCategory());
		builder.eq(b.degrees, filterTO.getDegrees());
		builder.eq(d.alcohol, filterTO.getAlcohol());
		builder.eq(b.ibu, filterTO.getIbu());
		builder.eq(b.maltType, filterTO.getMaltType());
		return builder.getBuilder();
	}

	@Override
	public long countBeers(BeerOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateBeers(filterTO)).fetchCount();
	}

	@Override
	public List<BeerOverviewTO> findBeers(BeerOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order) {
		JPAQuery<BeerOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		QuerydslUtil.applyPagination(pageable, query);
		return query
				.select(new QBeerOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, b.id, b.brewery,
						b.ibu, b.degrees, b.category, b.maltType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateBeers(filterTO)).orderBy(order).fetch();
	}

	@Override
	public BeerTO findBeerById(Long id) {
		JPAQuery<BeerTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		return query
				.select(new QBeerTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country, b.id,
						b.brewery, b.ibu, b.degrees, b.category, b.maltType, b.malts, b.hops))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}
}

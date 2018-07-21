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
import cz.gattserver.grass3.drinks.model.domain.QRumInfo;
import cz.gattserver.grass3.drinks.model.domain.QWhiskeyInfo;
import cz.gattserver.grass3.drinks.model.domain.QWineInfo;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.QBeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.QBeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.QRumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.QRumTO;
import cz.gattserver.grass3.drinks.model.interfaces.QWhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.QWhiskeyTO;
import cz.gattserver.grass3.drinks.model.interfaces.QWineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.QWineTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Repository
public class DrinkRepositoryCustomImpl implements DrinkRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	/*
	 * Piva
	 */

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

	/*
	 * Rumy
	 */

	private Predicate createPredicateRums(RumOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.RUM);
		builder.eq(b.rumType, filterTO.getRumType());
		builder.iLike(d.name, filterTO.getName());
		builder.iLike(d.country, filterTO.getCountry());
		builder.eq(b.years, filterTO.getYears());
		return builder.getBuilder();
	}

	@Override
	public long countRums(RumOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateRums(filterTO)).fetchCount();
	}

	@Override
	public List<RumOverviewTO> findRums(RumOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order) {
		JPAQuery<RumOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		QuerydslUtil.applyPagination(pageable, query);
		return query
				.select(new QRumOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, b.id, b.years,
						b.rumType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateRums(filterTO)).orderBy(order).fetch();
	}

	@Override
	public RumTO findRumById(Long id) {
		JPAQuery<RumTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		return query.select(new QRumTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country,
				b.id, b.years, b.rumType)).from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}

	/*
	 * Whiskey
	 */

	private Predicate createPredicateWhiskeys(WhiskeyOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.WHISKY);
		builder.eq(b.whiskeyType, filterTO.getWhiskeyType());
		builder.iLike(d.name, filterTO.getName());
		builder.iLike(d.country, filterTO.getCountry());
		builder.eq(b.years, filterTO.getYears());
		return builder.getBuilder();
	}

	@Override
	public long countWhiskeys(WhiskeyOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWhiskeys(filterTO)).fetchCount();
	}

	@Override
	public List<WhiskeyOverviewTO> findWhiskeys(WhiskeyOverviewTO filterTO, PageRequest pageable,
			OrderSpecifier<?>[] order) {
		JPAQuery<WhiskeyOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		QuerydslUtil.applyPagination(pageable, query);
		return query
				.select(new QWhiskeyOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, b.id, b.years,
						b.whiskeyType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWhiskeys(filterTO)).orderBy(order)
				.fetch();
	}

	@Override
	public WhiskeyTO findWhiskeyById(Long id) {
		JPAQuery<WhiskeyTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		return query
				.select(new QWhiskeyTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country,
						b.id, b.years, b.whiskeyType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}

	/*
	 * Wine
	 */

	private Predicate createPredicateWines(WineOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.WHISKY);
		builder.eq(b.wineType, filterTO.getWineType());
		builder.iLike(d.name, filterTO.getName());
		builder.iLike(d.country, filterTO.getCountry());
		builder.iLike(b.winery, filterTO.getWinery());
		builder.eq(b.year, filterTO.getYear());
		return builder.getBuilder();
	}

	@Override
	public long countWines(WineOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWines(filterTO)).fetchCount();
	}

	@Override
	public List<WineOverviewTO> findWines(WineOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order) {
		JPAQuery<WineOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		QuerydslUtil.applyPagination(pageable, query);
		return query
				.select(new QWineOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, b.id, b.winery,
						b.year, b.wineType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWines(filterTO)).orderBy(order).fetch();
	}

	@Override
	public WineTO findWineById(Long id) {
		JPAQuery<WineTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		return query
				.select(new QWineTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country, b.id,
						b.winery, b.year, b.wineType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}
}

package cz.gattserver.grass3.drinks.model.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;

public interface DrinkRepositoryCustom {

	/*
	 * Piva
	 */

	long countBeers(BeerOverviewTO filterTO);

	List<BeerOverviewTO> findBeers(BeerOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order);

	BeerTO findBeerById(Long id);

	/*
	 * Rumy
	 */

	long countRums(RumOverviewTO filterTO);

	List<RumOverviewTO> findRums(RumOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order);

	RumTO findRumById(Long id);

	/*
	 * Whiskey
	 */

	long countWhiskeys(WhiskeyOverviewTO filterTO);

	List<WhiskeyOverviewTO> findWhiskeys(WhiskeyOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order);

	WhiskeyTO findWhiskeyById(Long id);

	/*
	 * Vína
	 */

	long countWines(WineOverviewTO filterTO);

	List<WineOverviewTO> findWines(WineOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order);

	WineTO findWineById(Long id);
}

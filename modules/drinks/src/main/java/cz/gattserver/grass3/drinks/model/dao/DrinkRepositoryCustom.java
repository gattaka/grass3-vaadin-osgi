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

	long countBeers(String filter);

	List<BeerOverviewTO> findBeers(BeerOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<BeerOverviewTO> findBeers(String filter, PageRequest pageable);

	BeerTO findBeerById(Long id);

	/*
	 * Rumy
	 */

	long countRums(RumOverviewTO filterTO);

	long countRums(String filter);

	List<RumOverviewTO> findRums(RumOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<RumOverviewTO> findRums(String filter, PageRequest pageable);

	RumTO findRumById(Long id);

	/*
	 * Whiskey
	 */

	long countWhiskeys(WhiskeyOverviewTO filterTO);

	long countWhiskeys(String filter);

	List<WhiskeyOverviewTO> findWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<WhiskeyOverviewTO> findWhiskeys(String filter, PageRequest pageable);

	WhiskeyTO findWhiskeyById(Long id);

	/*
	 * Vína
	 */

	long countWines(WineOverviewTO filterTO);

	List<WineOverviewTO> findWines(WineOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	WineTO findWineById(Long id);
}

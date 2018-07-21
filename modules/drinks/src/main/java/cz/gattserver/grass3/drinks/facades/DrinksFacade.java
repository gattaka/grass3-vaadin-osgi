package cz.gattserver.grass3.drinks.facades;

import java.util.List;

import com.vaadin.data.provider.QuerySortOrder;

import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;

public interface DrinksFacade {

	/**
	 * Smaže nápoj
	 * 
	 * @param id
	 */
	void deleteDrink(Long id);

	/*
	 * Piva
	 */

	/**
	 * Získá počet piv v DB
	 * 
	 * @param filterTO
	 */
	int countBeers(BeerOverviewTO filterTO);

	/**
	 * Získá všechny piva
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<BeerOverviewTO> getBeers(BeerOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá pivo dle id
	 * 
	 * @param id
	 */
	BeerTO getBeerById(Long id);

	/**
	 * Založ/uprav nové pivo
	 * 
	 * @param to
	 */
	BeerTO saveBeer(BeerTO to);

	/*
	 * Rumy
	 */

	/**
	 * Získá počet rumů v DB
	 * 
	 * @param filterTO
	 */
	int countRums(RumOverviewTO filterTO);

	/**
	 * Získá všechny rumy
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<RumOverviewTO> getRums(RumOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá rum dle id
	 * 
	 * @param id
	 */
	RumTO getRumById(Long id);

	/**
	 * Založ/uprav nový rum
	 * 
	 * @param to
	 */
	RumTO saveRum(RumTO to);

	/*
	 * Whiskey
	 */

	/**
	 * Získá počet whiskey v DB
	 * 
	 * @param filterTO
	 */
	int countWhiskeys(WhiskeyOverviewTO filterTO);

	/**
	 * Získá všechny whiskey
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<WhiskeyOverviewTO> getWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder);

	/**
	 * Získá whiskey dle id
	 * 
	 * @param id
	 */
	WhiskeyTO getWhiskeyById(Long id);

	/**
	 * Založ/uprav novou whiskey
	 * 
	 * @param to
	 */
	WhiskeyTO saveWhiskey(WhiskeyTO to);

	/*
	 * Vína
	 */

	/**
	 * Získá počet vín v DB
	 * 
	 * @param filterTO
	 */
	int countWines(WineOverviewTO filterTO);

	/**
	 * Získá všechny vína
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<WineOverviewTO> getWines(WineOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá víno dle id
	 * 
	 * @param id
	 */
	WineTO getWineById(Long id);

	/**
	 * Založ/uprav nové víno
	 * 
	 * @param to
	 */
	WineTO saveWine(WineTO to);

}

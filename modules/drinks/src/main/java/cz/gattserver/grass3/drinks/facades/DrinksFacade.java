package cz.gattserver.grass3.drinks.facades;

import java.util.List;

import com.vaadin.data.provider.QuerySortOrder;

import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;

public interface DrinksFacade {

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
	 * Založí/uprav novou nápoj
	 * 
	 * @param drinkDTO
	 */
	BeerTO saveBeer(BeerTO drinkDTO);

	/**
	 * Smaže nápoj
	 * 
	 * @param id
	 */
	void deleteDrink(Long id);

}

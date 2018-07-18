package cz.gattserver.grass3.drinks.facades;

import java.util.List;

import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;

public interface DrinksFacade {

	/**
	 * Získá počet nápojů v DB
	 */
	public int getDrinksCount();

	/**
	 * Získá všechny nápoje
	 * 
	 * @param filterTO
	 */
	public List<DrinkOverviewTO> getDrinks(DrinkOverviewTO filterTO);

	/**
	 * Získá nápoj dle id
	 */
	public DrinkTO getDrinkById(Long id);

	/**
	 * Založí/uprav novou nápoj
	 */
	public DrinkTO saveDrink(DrinkTO drinkDTO);

	/**
	 * Smaže nápoj
	 * 
	 * @param id
	 */
	public void deleteDrink(Long id);

}

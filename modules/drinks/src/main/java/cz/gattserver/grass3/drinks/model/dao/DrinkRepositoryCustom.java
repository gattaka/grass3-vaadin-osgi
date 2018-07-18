package cz.gattserver.grass3.drinks.model.dao;

import java.util.List;

import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;

public interface DrinkRepositoryCustom {

	List<Drink> findAllOrderByName(DrinkOverviewTO filterTO);
}

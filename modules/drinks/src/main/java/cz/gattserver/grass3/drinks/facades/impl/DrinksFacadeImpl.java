package cz.gattserver.grass3.drinks.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.dao.DrinkRepository;
import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.drinks.util.Mapper;

@Transactional
@Component
public class DrinksFacadeImpl implements DrinksFacade {

	@Autowired
	private Mapper mapper;

	@Autowired
	private DrinkRepository drinkRepository;

	@Override
	public List<DrinkOverviewTO> getDrinks(DrinkOverviewTO filterTO) {
		List<Drink> drinks = drinkRepository.findAllOrderByName(filterTO);
		if (drinks == null)
			return null;
		return mapper.mapDrinks(drinks);
	}

	@Override
	public DrinkTO getDrinkById(Long id) {
		Drink drink = drinkRepository.findOne(id);
		if (drink == null)
			return null;
		return mapper.mapDrink(drink);
	}

	@Override
	public DrinkTO saveDrink(DrinkTO to) {
		Drink drinks = mapper.mapDrink(to);
		drinks = drinkRepository.save(drinks);
		return mapper.mapDrink(drinks);
	}

	@Override
	public int getDrinksCount() {
		return (int) drinkRepository.count();
	}

	@Override
	public void deleteDrink(Long id) {
		drinkRepository.delete(id);
	}

}

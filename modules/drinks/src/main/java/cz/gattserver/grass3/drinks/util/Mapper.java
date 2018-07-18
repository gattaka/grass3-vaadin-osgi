package cz.gattserver.grass3.drinks.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená,
 * že před tímto mapperem nedošlo k uzavření session, ve které byl původní
 * objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během
 * mapování tak může docházet k dotazům na DB, které produkují tyto proxy
 * objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("drinkMapper")
public class Mapper {

	/**
	 * Převede {@link Drink} na {@link DrinkTO}
	 * 
	 * @param e
	 * @return
	 */
	public DrinkTO mapDrink(Drink e) {
		if (e == null)
			return null;

		DrinkTO drink = new DrinkTO();

		drink.setId(e.getId());
		drink.setName(e.getName());
		drink.setDescription(e.getDescription());
		drink.setImage(e.getImage());
		drink.setRating(e.getRating());
		drink.setType(e.getType());

		return drink;
	}

	/**
	 * Převede list {@link DrinkTO} na list {@link DrinkTO}
	 * 
	 * @param drinks
	 * @return
	 */
	public List<DrinkOverviewTO> mapDrinks(Collection<Drink> drinks) {
		if (drinks == null)
			return new ArrayList<>();

		List<DrinkOverviewTO> drinksTOs = new ArrayList<DrinkOverviewTO>();
		for (Drink drink : drinks)
			drinksTOs.add(new DrinkOverviewTO(drink.getId(), drink.getName(), drink.getType(), drink.getRating()));

		return drinksTOs;
	}

	/**
	 * Převede {@link DrinkTO} na {@link Drink}
	 * 
	 * @param e
	 * @return
	 */
	public Drink mapDrink(DrinkTO e) {
		if (e == null)
			return null;

		Drink drink = new Drink();

		drink.setId(e.getId());
		drink.setName(e.getName());
		drink.setDescription(e.getDescription());
		drink.setImage(e.getImage());
		drink.setRating(e.getRating());
		drink.setType(e.getType());

		return drink;
	}

}
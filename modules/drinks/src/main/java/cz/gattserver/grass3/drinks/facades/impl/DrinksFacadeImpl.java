package cz.gattserver.grass3.drinks.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.data.provider.QuerySortOrder;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.dao.BeerInfoRepository;
import cz.gattserver.grass3.drinks.model.dao.DrinkRepository;
import cz.gattserver.grass3.drinks.model.domain.BeerInfo;
import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Transactional
@Component
public class DrinksFacadeImpl implements DrinksFacade {

	@Autowired
	private DrinkRepository drinkRepository;

	@Autowired
	private BeerInfoRepository beerInfoRepository;

	@Override
	public int countBeers(BeerOverviewTO filterTO) {
		return (int) drinkRepository.countBeers(filterTO);
	}

	@Override
	public List<BeerOverviewTO> getBeers(BeerOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findBeers(filterTO, QuerydslUtil.transformOffsetLimit(offset, limit),
				QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public BeerTO getBeerById(Long id) {
		return drinkRepository.findBeerById(id);
	}

	@Override
	public BeerTO saveBeer(BeerTO to) {
		BeerInfo b = new BeerInfo(to.getBrewery(), to.getIbu(), to.getDegrees(), to.getCategory(), to.getMaltType(),
				to.getMalts(), to.getHops());
		b.setId(to.getInfoId());
		b = beerInfoRepository.save(b);

		Drink d = new Drink(to.getName(), DrinkType.BEER, to.getRating(), to.getImage(), to.getDescription(),
				to.getAlcohol(), to.getCountry(), b.getId());
		d.setId(to.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());

		return to;
	}

	@Override
	public void deleteDrink(Long id) {
		drinkRepository.delete(id);
	}

}

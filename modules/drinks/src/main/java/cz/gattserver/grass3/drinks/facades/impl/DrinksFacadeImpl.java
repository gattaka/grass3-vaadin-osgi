package cz.gattserver.grass3.drinks.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.data.provider.QuerySortOrder;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.dao.BeerInfoRepository;
import cz.gattserver.grass3.drinks.model.dao.DrinkRepository;
import cz.gattserver.grass3.drinks.model.dao.RumInfoRepository;
import cz.gattserver.grass3.drinks.model.dao.WhiskeyInfoRepository;
import cz.gattserver.grass3.drinks.model.dao.WineInfoRepository;
import cz.gattserver.grass3.drinks.model.domain.BeerInfo;
import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.domain.RumInfo;
import cz.gattserver.grass3.drinks.model.domain.WhiskeyInfo;
import cz.gattserver.grass3.drinks.model.domain.WineInfo;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.model.util.QuerydslUtil;

@Transactional
@Component
public class DrinksFacadeImpl implements DrinksFacade {

	@Autowired
	private DrinkRepository drinkRepository;

	@Autowired
	private BeerInfoRepository beerInfoRepository;

	@Autowired
	private RumInfoRepository rumInfoRepository;

	@Autowired
	private WhiskeyInfoRepository whiskeyInfoRepository;

	@Autowired
	private WineInfoRepository wineInfoRepository;

	@Override
	public void deleteDrink(Long id) {
		drinkRepository.delete(id);
	}

	/*
	 * Piva
	 */
	@Override
	public int countBeers() {
		return (int) drinkRepository.countBeers(null);
	}

	@Override
	public List<BeerOverviewTO> getBeers(int page, int size) {
		return drinkRepository.findBeers(null, new PageRequest(page, size), null);
	}

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

	/*
	 * Rumy
	 */

	@Override
	public int countRums() {
		return (int) drinkRepository.countRums(null);
	}

	@Override
	public List<RumOverviewTO> getRums(int page, int size) {
		return drinkRepository.findRums(null, new PageRequest(page, size), null);
	}

	@Override
	public int countRums(RumOverviewTO filterTO) {
		return (int) drinkRepository.countRums(filterTO);
	}

	@Override
	public List<RumOverviewTO> getRums(RumOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder) {
		return drinkRepository.findRums(filterTO, QuerydslUtil.transformOffsetLimit(offset, limit),
				QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public RumTO getRumById(Long id) {
		return drinkRepository.findRumById(id);
	}

	@Override
	public RumTO saveRum(RumTO to) {
		RumInfo b = new RumInfo(to.getYears(), to.getRumType());
		b.setId(to.getInfoId());
		b = rumInfoRepository.save(b);

		Drink d = new Drink(to.getName(), DrinkType.RUM, to.getRating(), to.getImage(), to.getDescription(),
				to.getAlcohol(), to.getCountry(), b.getId());
		d.setId(to.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());

		return to;
	}

	/*
	 * Whiskey
	 */

	@Override
	public int countWhiskeys() {
		return (int) drinkRepository.countWhiskeys(null);
	}

	@Override
	public List<WhiskeyOverviewTO> getWhiskeys(int page, int size) {
		return drinkRepository.findWhiskeys(null, new PageRequest(page, size), null);
	}

	@Override
	public int countWhiskeys(WhiskeyOverviewTO filterTO) {
		return (int) drinkRepository.countWhiskeys(filterTO);
	}

	@Override
	public List<WhiskeyOverviewTO> getWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findWhiskeys(filterTO, QuerydslUtil.transformOffsetLimit(offset, limit),
				QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public WhiskeyTO getWhiskeyById(Long id) {
		return drinkRepository.findWhiskeyById(id);
	}

	@Override
	public WhiskeyTO saveWhiskey(WhiskeyTO to) {
		WhiskeyInfo b = new WhiskeyInfo(to.getYears(), to.getWhiskeyType());
		b.setId(to.getInfoId());
		b = whiskeyInfoRepository.save(b);

		Drink d = new Drink(to.getName(), DrinkType.WHISKY, to.getRating(), to.getImage(), to.getDescription(),
				to.getAlcohol(), to.getCountry(), b.getId());
		d.setId(to.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());

		return to;
	}

	/*
	 * Vína
	 */

	@Override
	public int countWines() {
		return (int) drinkRepository.countWines(null);
	}

	@Override
	public List<WineOverviewTO> getWines(int page, int size) {
		return drinkRepository.findWines(null, new PageRequest(page, size), null);
	}

	@Override
	public int countWines(WineOverviewTO filterTO) {
		return (int) drinkRepository.countWines(filterTO);
	}

	@Override
	public List<WineOverviewTO> getWines(WineOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findWines(filterTO, QuerydslUtil.transformOffsetLimit(offset, limit),
				QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public WineTO getWineById(Long id) {
		return drinkRepository.findWineById(id);
	}

	@Override
	public WineTO saveWine(WineTO to) {
		WineInfo b = new WineInfo(to.getWinery(), to.getYear(), to.getWineType());
		b.setId(to.getInfoId());
		b = wineInfoRepository.save(b);
		to.setInfoId(b.getId());

		Drink d = new Drink(to.getName(), DrinkType.WINE, to.getRating(), to.getImage(), to.getDescription(),
				to.getAlcohol(), to.getCountry(), b.getId());
		d.setId(to.getId());
		d = drinkRepository.save(d);
		to.setId(d.getId());

		return to;
	}

}

package cz.gattserver.grass3.drinks.model.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;

public interface DrinkRepositoryCustom {

	long countBeers(BeerOverviewTO filterTO);

	List<BeerOverviewTO> findBeers(BeerOverviewTO filterTO, PageRequest pageable, OrderSpecifier<?>[] order);

	BeerTO findBeerById(Long id);

}

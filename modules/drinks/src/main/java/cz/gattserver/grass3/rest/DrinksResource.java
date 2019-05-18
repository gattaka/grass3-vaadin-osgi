package cz.gattserver.grass3.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;

@Controller
@RequestMapping("/drinks")
public class DrinksResource {

	@Autowired
	private DrinksFacade drinksFacade;

	/*
	 * Pivo
	 */

	@RequestMapping("/beer-list")
	public ResponseEntity<List<BeerOverviewTO>> beerList(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		int count = drinksFacade.countBeers(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksFacade.getBeers(filter, page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/beer-count")
	public ResponseEntity<Integer> beerCount(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(drinksFacade.countBeers(filter), HttpStatus.OK);
	}

	@RequestMapping("/beer")
	public @ResponseBody BeerTO beer(@RequestParam(value = "id", required = true) Long id) {
		return drinksFacade.getBeerById(id);
	}

	/*
	 * Rum
	 */

	@RequestMapping("/rum-list")
	public ResponseEntity<List<RumOverviewTO>> rumList(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		int count = drinksFacade.countRums(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksFacade.getRums(filter,page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/rum-count")
	public ResponseEntity<Integer> rumCount(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(drinksFacade.countRums(filter), HttpStatus.OK);
	}

	@RequestMapping("/rum")
	public @ResponseBody RumTO rum(@RequestParam(value = "id", required = true) Long id) {
		return drinksFacade.getRumById(id);
	}

	/*
	 * Whiskey
	 */

	@RequestMapping("/whiskey-list")
	public ResponseEntity<List<WhiskeyOverviewTO>> whiskeyList(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		int count = drinksFacade.countWhiskeys(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksFacade.getWhiskeys(filter,page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/whiskey-count")
	public ResponseEntity<Integer> whiskeyCount(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(drinksFacade.countWhiskeys(filter), HttpStatus.OK);
	}

	@RequestMapping("/whiskey")
	public @ResponseBody WhiskeyTO whiskey(@RequestParam(value = "id", required = true) Long id) {
		return drinksFacade.getWhiskeyById(id);
	}

	/*
	 * Wine
	 */

	@RequestMapping("/wine-list")
	public ResponseEntity<List<WineOverviewTO>> wineList(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		int count = drinksFacade.countWines();
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksFacade.getWines(page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/wine-count")
	public ResponseEntity<Integer> wineCount() {
		return new ResponseEntity<>(drinksFacade.countWines(), HttpStatus.OK);
	}

	@RequestMapping("/wine")
	public @ResponseBody WineTO wine(@RequestParam(value = "id", required = true) Long id) {
		return drinksFacade.getWineById(id);
	}

}

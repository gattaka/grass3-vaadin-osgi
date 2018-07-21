package cz.gattserver.grass3.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.domain.WineType;

public class WineOverviewTO extends DrinkOverviewTO {

	/**
	 * DB id
	 */
	private Long id;

	/**
	 * Vinařství
	 */
	private String winery;

	/**
	 * Ročník
	 */
	private Integer year;

	/**
	 * Typ vína
	 */
	private WineType wineType;

	public WineOverviewTO() {
	}

	@QueryProjection
	public WineOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, String winery, Integer year, WineType wineType) {
		super(id, name, type, rating, alcohol, country);
		this.id = infoId;
		this.winery = winery;
		this.year = year;
		this.wineType = wineType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWinery() {
		return winery;
	}

	public void setWinery(String winery) {
		this.winery = winery;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public WineType getWineType() {
		return wineType;
	}

	public void setWineType(WineType wineType) {
		this.wineType = wineType;
	}

}

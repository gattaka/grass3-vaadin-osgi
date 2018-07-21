package cz.gattserver.grass3.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;

public class WhiskeyOverviewTO extends DrinkOverviewTO {

	/**
	 * DB id
	 */
	private Long id;

	/**
	 * Stáří
	 */
	private Integer years;

	/**
	 * Typ whisky
	 */
	private WhiskeyType whiskeyType;

	public WhiskeyOverviewTO() {
	}

	@QueryProjection
	public WhiskeyOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, Integer years, WhiskeyType whiskeyType) {
		super(id, name, type, rating, alcohol, country);
		this.id = infoId;
		this.years = years;
		this.whiskeyType = whiskeyType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WhiskeyType getWhiskeyType() {
		return whiskeyType;
	}

	public void setWhiskeyType(WhiskeyType whiskeyType) {
		this.whiskeyType = whiskeyType;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

}

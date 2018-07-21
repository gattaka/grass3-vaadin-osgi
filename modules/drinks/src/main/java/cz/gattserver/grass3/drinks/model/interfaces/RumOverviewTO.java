package cz.gattserver.grass3.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.domain.RumType;

public class RumOverviewTO extends DrinkOverviewTO {

	/**
	 * DB id
	 */
	private Long id;

	/**
	 * Stáří
	 */
	private Integer years;

	/**
	 * Typ rumu
	 */
	private RumType rumType;

	public RumOverviewTO() {
	}

	@QueryProjection
	public RumOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, Integer years, RumType rumType) {
		super(id, name, type, rating, alcohol, country);
		this.id = infoId;
		this.years = years;
		this.rumType = rumType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RumType getRumType() {
		return rumType;
	}

	public void setRumType(RumType rumType) {
		this.rumType = rumType;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

}

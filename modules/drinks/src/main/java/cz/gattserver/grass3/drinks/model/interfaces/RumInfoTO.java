package cz.gattserver.grass3.drinks.model.interfaces;

import cz.gattserver.grass3.drinks.model.domain.RumType;

public class RumInfoTO {

	/**
	 * DB id
	 */
	private Long id;

	/**
	 * Stáří
	 */
	private Integer years;

	private RumType rumType;

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

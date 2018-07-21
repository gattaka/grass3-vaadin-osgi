package cz.gattserver.grass3.drinks.model.interfaces;

import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;

public class WhiskeyInfoTO {

	/**
	 * DB id
	 */
	private Long id;

	/**
	 * Stáří
	 */
	private Integer years;

	private WhiskeyType whiskeyType;

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

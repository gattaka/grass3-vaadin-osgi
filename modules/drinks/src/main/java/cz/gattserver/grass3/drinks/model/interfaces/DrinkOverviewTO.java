package cz.gattserver.grass3.drinks.model.interfaces;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;

public class DrinkOverviewTO {

	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Typ
	 */
	private DrinkType type;

	/**
	 * Hodnocení
	 */
	private Integer rating;

	public DrinkOverviewTO() {
	}

	public DrinkOverviewTO(Long id, String name, DrinkType type, Integer rating) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.rating = rating;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DrinkType getType() {
		return type;
	}

	public void setType(DrinkType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DrinkOverviewTO))
			return false;
		return ((DrinkOverviewTO) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}

package cz.gattserver.grass3.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.domain.MaltType;

public class BeerOverviewTO extends DrinkOverviewTO {

	/**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Pivovar
	 */
	private String brewery;

	/**
	 * Hořkost -- International Bitterness Units scale (IBU)
	 */
	private Integer ibu;

	/**
	 * Stupňovitost
	 */
	private Integer degrees;

	/**
	 * Kategorie -- IPA, APA, Ležák apod.
	 */
	private String category;

	/**
	 * Druh sladu
	 */
	private MaltType maltType;

	public BeerOverviewTO() {
	}

	@QueryProjection
	public BeerOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, String brewery, Integer ibu, Integer degrees, String category, MaltType maltType) {
		super(id, name, type, rating, alcohol, country);
		this.infoId = infoId;
		this.brewery = brewery;
		this.ibu = ibu;
		this.degrees = degrees;
		this.category = category;
		this.maltType = maltType;
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public String getBrewery() {
		return brewery;
	}

	public void setBrewery(String brewery) {
		this.brewery = brewery;
	}

	public Integer getIbu() {
		return ibu;
	}

	public void setIbu(Integer ibu) {
		this.ibu = ibu;
	}

	public Integer getDegrees() {
		return degrees;
	}

	public void setDegrees(Integer degrees) {
		this.degrees = degrees;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public MaltType getMaltType() {
		return maltType;
	}

	public void setMaltType(MaltType maltType) {
		this.maltType = maltType;
	}

}

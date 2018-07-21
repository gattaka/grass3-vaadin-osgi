package cz.gattserver.grass3.drinks.model.domain;

/**
 * Typ sladu
 * 
 * @author gattaka
 *
 */
public enum MaltType {

	BARLEY("Ječmen"), WHEAT("Pšenice");

	private String caption;

	private MaltType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}

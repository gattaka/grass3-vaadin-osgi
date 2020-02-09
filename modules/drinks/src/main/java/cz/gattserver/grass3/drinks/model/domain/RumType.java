package cz.gattserver.grass3.drinks.model.domain;

public enum RumType {

	BLANCO("Světlý"), DARK("Tmavý");

	private String caption;

	private RumType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}
}

package cz.gattserver.grass3.songs.model.domain;

public enum Instrument {

	GUITAR("Kytara");

	private String caption;

	private Instrument(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}
}

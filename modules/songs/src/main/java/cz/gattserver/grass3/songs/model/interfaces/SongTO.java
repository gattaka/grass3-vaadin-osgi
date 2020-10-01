package cz.gattserver.grass3.songs.model.interfaces;

public class SongTO extends SongOverviewTO {

	/**
	 * Text
	 */
	private String text;

	public SongTO() {
	}

	public SongTO(String name, String author, Integer year, String text, Long id, Boolean publicated) {
		super(name, author, year, id, publicated);
		this.text = text;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SongTO))
			return false;
		return ((SongTO) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

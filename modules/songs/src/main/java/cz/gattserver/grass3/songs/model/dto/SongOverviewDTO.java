package cz.gattserver.grass3.songs.model.dto;

import cz.gattserver.grass3.songs.model.domain.Song;

public class SongOverviewDTO {

	/**
	 * Název
	 */
	private String name;

	/**
	 * Autor
	 */
	private String author;

	/**
	 * DB id
	 */
	private Long id;

	public SongOverviewDTO(String name, String author, Long id) {
		super();
		this.name = name;
		this.author = author;
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Song))
			return false;
		return ((Song) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}

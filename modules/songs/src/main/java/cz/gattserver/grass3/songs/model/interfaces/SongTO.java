package cz.gattserver.grass3.songs.model.interfaces;

import cz.gattserver.grass3.songs.model.domain.Song;

public class SongTO {

	/**
	 * Název
	 */
	private String name;

	/**
	 * Autor
	 */
	private String author;

	/**
	 * Rok
	 */
	private Integer year;

	/**
	 * Text
	 */
	private String text;

	/**
	 * DB id
	 */
	private Long id;

	public SongTO() {
	}

	public SongTO(String name, String author, Integer year, String text, Long id) {
		super();
		this.name = name;
		this.author = author;
		this.year = year;
		this.text = text;
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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

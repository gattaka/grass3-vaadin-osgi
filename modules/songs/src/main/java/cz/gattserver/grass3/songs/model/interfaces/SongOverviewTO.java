package cz.gattserver.grass3.songs.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import cz.gattserver.grass3.songs.model.domain.Song;

public class SongOverviewTO {

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
	 * DB id
	 */
	private Long id;

	/**
	 * Náhled
	 */
	private String preview;

	public SongOverviewTO() {
	}

	public SongOverviewTO(String name, String author, Integer year, Long id) {
		super();
		this.name = name;
		this.author = author;
		this.year = year;
		this.id = id;
	}

	@QueryProjection
	public SongOverviewTO(String name, String author, Integer year, Long id, String preview) {
		super();
		this.name = name;
		this.author = author;
		this.year = year;
		this.id = id;
		this.preview = preview;
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

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

}

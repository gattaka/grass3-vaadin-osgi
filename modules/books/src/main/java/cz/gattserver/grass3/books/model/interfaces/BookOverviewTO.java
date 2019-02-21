package cz.gattserver.grass3.books.model.interfaces;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

public class BookOverviewTO {

	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Autor
	 */
	private String author;

	/**
	 * Hodnocení
	 */
	private Double rating;

	/**
	 * Kdy byla kniha vydána
	 */
	private LocalDate released;

	public BookOverviewTO() {
	}

	@QueryProjection
	public BookOverviewTO(Long id, String name, String author, Double rating, LocalDate released) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
		this.rating = rating;
		this.released = released;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDate getReleased() {
		return released;
	}

	public void setReleased(LocalDate released) {
		this.released = released;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BookOverviewTO))
			return false;
		return ((BookOverviewTO) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}

package org.myftp.gattserver.grass3.model.dto;

public class QuoteDTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Obsah
	 */
	private String name;

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

}

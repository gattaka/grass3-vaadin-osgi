package org.myftp.gattserver.grass3.pg.dto;

public class PhotoDescriptionDTO {

	/**
	 * Název fotografie
	 */
	private String name;

	/**
	 * Popis fotografie
	 */
	private String description;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public PhotoDescriptionDTO() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

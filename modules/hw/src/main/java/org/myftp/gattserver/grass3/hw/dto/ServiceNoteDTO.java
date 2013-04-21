package org.myftp.gattserver.grass3.hw.dto;

/**
 * Údaj o opravě, změně součástí apod.
 */
public class ServiceNoteDTO {

	/**
	 * Identifikátor změny
	 */
	private Long id;

	/**
	 * Popis změny
	 */
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

package org.myftp.gattserver.grass3.medic.dto;

import javax.validation.constraints.NotNull;

public class MedicamentDTO {

	private Long id;

	/**
	 * Název léku
	 */
	@NotNull
	private String name;

	/**
	 * Snášenlivost
	 */
	@NotNull
	private String tolerance;

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

	public String getTolerance() {
		return tolerance;
	}

	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}

}

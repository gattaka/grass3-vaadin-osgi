package org.myftp.gattserver.grass3.medic.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MedicamentDTO {

	private Long id;

	/**
	 * Název léku
	 */
	@NotNull
	@Size(min = 1)
	private String name = "";

	/**
	 * Snášenlivost
	 */
	@NotNull
	@Size(min = 1)
	private String tolerance = "V pořádku";

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

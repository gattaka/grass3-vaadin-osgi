package cz.gattserver.grass3.medic.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cz.gattserver.grass3.model.dto.Identifiable;

public class MedicamentDTO implements Identifiable{

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MedicamentDTO) {
			MedicamentDTO dto = (MedicamentDTO) obj;
			if (dto.getId() == null)
				return id == null;
			else
				return dto.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}

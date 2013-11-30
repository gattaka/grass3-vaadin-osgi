package org.myftp.gattserver.grass3.medic.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.myftp.gattserver.grass3.model.dto.Identifiable;

public class PhysicianDTO implements Identifiable{

	private Long id;

	/**
	 * Jméno
	 */
	@NotNull
	@Size(min = 1)
	private String name = "";

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

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhysicianDTO) {
			PhysicianDTO dto = (PhysicianDTO) obj;
			if (dto.getName() == null)
				return name == null;
			else
				return dto.getName().equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}

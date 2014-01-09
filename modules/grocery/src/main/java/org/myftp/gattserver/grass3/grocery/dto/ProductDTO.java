package org.myftp.gattserver.grass3.grocery.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.myftp.gattserver.grass3.model.dto.Identifiable;

public class ProductDTO implements Identifiable {

	private Long id;

	/**
	 * Název produktu
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
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof ProductDTO) {
			return id == ((ProductDTO) obj).getId();
		}
		return false;
	}
}

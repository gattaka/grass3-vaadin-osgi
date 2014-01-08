package org.myftp.gattserver.grass3.grocery.dto;

import org.myftp.gattserver.grass3.model.dto.Identifiable;

public class ProductDTO implements Identifiable {

	private Long id;

	/**
	 * Název produktu
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

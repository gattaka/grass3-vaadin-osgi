package org.myftp.gattserver.grass3.grocery.dto;

public class ShopDTO {

	private Long id;

	/**
	 * Název obchodu
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

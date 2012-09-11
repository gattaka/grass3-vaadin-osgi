package org.myftp.gattserver.grass3.model.dto;

import java.io.Serializable;

public class QuoteDTO implements Serializable {

	private static final long serialVersionUID = 4648523987418261988L;

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

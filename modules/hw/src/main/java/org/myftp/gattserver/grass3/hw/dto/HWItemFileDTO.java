package org.myftp.gattserver.grass3.hw.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Dokument k HW (fotka, manuál)
 */
public class HWItemFileDTO {

	/**
	 * Identifikátor souboru
	 */
	private Long id;

	/**
	 * Popis souboru
	 */
	@NotNull
	@Size(min = 1)
	private String description;

	/**
	 * Link na soubor
	 */
	@NotNull
	@Size(min = 1)
	private String link;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

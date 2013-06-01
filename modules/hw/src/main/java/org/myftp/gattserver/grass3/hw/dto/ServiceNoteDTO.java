package org.myftp.gattserver.grass3.hw.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Údaj o opravě, změně součástí apod.
 */
public class ServiceNoteDTO {

	/**
	 * Identifikátor změny
	 */
	private Long id;

	/**
	 * Datum události
	 */
	@NotNull
	private Date date;

	/**
	 * Popis změny
	 */
	@NotNull
	@Size(min = 1)
	private String description;

	/**
	 * Stav do kterého byl HW převeden v souvislosti s popisovanou událostí
	 */
	private HWItemState state;

	/**
	 * Součásti
	 */
	private HWItemDTO usedIn;

	public HWItemDTO getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(HWItemDTO usedIn) {
		this.usedIn = usedIn;
	}

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public HWItemState getState() {
		return state;
	}

	public void setState(HWItemState state) {
		this.state = state;
	}

}

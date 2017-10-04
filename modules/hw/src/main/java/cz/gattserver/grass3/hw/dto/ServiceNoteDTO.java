package cz.gattserver.grass3.hw.dto;

import java.time.LocalDate;

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
	private LocalDate date;

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
	private String usedInName;
	private Long usedInId;

	public String getUsedInName() {
		return usedInName;
	}

	public void setUsedInName(String usedInName) {
		this.usedInName = usedInName;
	}

	public Long getUsedInId() {
		return usedInId;
	}

	public void setUsedInId(Long usedInId) {
		this.usedInId = usedInId;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public HWItemState getState() {
		return state;
	}

	public void setState(HWItemState state) {
		this.state = state;
	}

}

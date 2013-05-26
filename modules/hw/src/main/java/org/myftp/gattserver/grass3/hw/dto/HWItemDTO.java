package org.myftp.gattserver.grass3.hw.dto;

import java.util.Date;
import java.util.List;

/**
 * HW Objekt
 */
public class HWItemDTO {

	/**
	 * Identifikátor hw
	 */
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Typ - klasifikace hw
	 */
	private List<HWItemTypeDTO> types;

	/**
	 * Datum zakoupení (získání)
	 */
	private Date purchaseDate;

	/**
	 * Datum vyhození, zničení, prodání
	 */
	private Date destructionDate;

	/**
	 * Cena
	 */
	private Integer price;

	/**
	 * Stav hw - funkční, rozbitý, poruchový, bližší popis
	 */
	private String state;

	/**
	 * Poznámky ke stavu hw - opravy apod.
	 */
	private List<ServiceNoteDTO> serviceNotes;

	/**
	 * Součásti
	 */
	private List<HWItemDTO> parts;

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

	public List<HWItemTypeDTO> getTypes() {
		return types;
	}

	public void setTypes(List<HWItemTypeDTO> types) {
		this.types = types;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Date getDestructionDate() {
		return destructionDate;
	}

	public void setDestructionDate(Date destructionDate) {
		this.destructionDate = destructionDate;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<ServiceNoteDTO> getServiceNotes() {
		return serviceNotes;
	}

	public void setServiceNotes(List<ServiceNoteDTO> serviceNotes) {
		this.serviceNotes = serviceNotes;
	}

	public List<HWItemDTO> getParts() {
		return parts;
	}

	public void setParts(List<HWItemDTO> parts) {
		this.parts = parts;
	}

}

package org.myftp.gattserver.grass3.hw.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * HW Objekt
 */
public class HWItem {

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
	private Set<HWItemType> types;

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
	private List<ServiceNote> serviceNotes;

	/**
	 * Součásti
	 */
	private Set<HWItem> parts;

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

	public Set<HWItemType> getTypes() {
		return types;
	}

	public void setTypes(Set<HWItemType> types) {
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

	public List<ServiceNote> getServiceNotes() {
		return serviceNotes;
	}

	public void setServiceNotes(List<ServiceNote> serviceNotes) {
		this.serviceNotes = serviceNotes;
	}

	public Set<HWItem> getParts() {
		return parts;
	}

	public void setParts(Set<HWItem> parts) {
		this.parts = parts;
	}

}

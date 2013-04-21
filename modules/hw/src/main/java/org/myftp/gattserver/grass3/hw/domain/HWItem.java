package org.myftp.gattserver.grass3.hw.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * HW Objekt
 */
@Entity
@Table(name = "HW_ITEM")
public class HWItem {

	/**
	 * Identifikátor hw
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Typ - klasifikace hw
	 */
	private HWItemType type;

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
	private int price;

	/**
	 * Stav hw - funkční, rozbitý, poruchový, bližší popis
	 */
	private String state;

	/**
	 * Poznámky ke stavu hw - opravy apod.
	 */
	@OneToMany
	private List<ServiceNote> serviceNotes;

	/**
	 * Součásti
	 */
	@OneToMany
	private Set<HWItem> parts;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HWItemType getType() {
		return type;
	}

	public void setType(HWItemType type) {
		this.type = type;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
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

package cz.gattserver.grass3.hw.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	@NotNull
	@Size(min = 1)
	private String name;

	/**
	 * Typ - klasifikace hw
	 */
	private Set<HWItemTypeDTO> types;

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
	@NotNull
	private HWItemState state;

	/**
	 * Poznámky ke stavu hw - opravy apod.
	 */
	private List<ServiceNoteDTO> serviceNotes;

	/**
	 * Počet let záruky
	 */
	private Integer warrantyYears;

	/**
	 * Součást celku
	 */
	private HWItemDTO usedIn;

	public HWItemDTO getUsedIn() {
		return usedIn;
	}

	public String getUsedInName() {
		if (usedIn != null)
			return usedIn.getName();
		return "";
	}

	public void setUsedIn(HWItemDTO usedIn) {
		this.usedIn = usedIn;
	}

	public Integer getWarrantyYears() {
		return warrantyYears;
	}

	public void setWarrantyYears(Integer warrantyYears) {
		this.warrantyYears = warrantyYears;
	}

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

	public Set<HWItemTypeDTO> getTypes() {
		return types;
	}

	public void setTypes(Set<HWItemTypeDTO> types) {
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

	public HWItemState getState() {
		return state;
	}

	public void setState(HWItemState state) {
		this.state = state;
	}

	public List<ServiceNoteDTO> getServiceNotes() {
		return serviceNotes;
	}

	public void setServiceNotes(List<ServiceNoteDTO> serviceNotes) {
		this.serviceNotes = serviceNotes;
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HWItemDTO) {
			return (((HWItemDTO) obj).getId().equals(id));
		} else
			return false;
	}
}

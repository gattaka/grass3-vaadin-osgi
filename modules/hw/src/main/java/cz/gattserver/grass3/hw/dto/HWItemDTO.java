package cz.gattserver.grass3.hw.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
	private LocalDate purchaseDate;

	/**
	 * Datum vyhození, zničení, prodání
	 */
	private LocalDate destructionDate;

	/**
	 * Cena
	 */
	private BigDecimal price;

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
	private HWItemOverviewDTO usedIn;

	/**
	 * Spravováno pro (spravuju tohle zařízení někomu?)
	 */
	private String supervizedFor;

	public String getSupervizedFor() {
		return supervizedFor;
	}

	public void setSupervizedFor(String supervizedFor) {
		this.supervizedFor = supervizedFor;
	}

	public HWItemOverviewDTO getUsedIn() {
		return usedIn;
	}

	public String getUsedInName() {
		if (usedIn != null)
			return usedIn.getName();
		return "";
	}

	public void setUsedIn(HWItemOverviewDTO usedIn) {
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

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public LocalDate getDestructionDate() {
		return destructionDate;
	}

	public void setDestructionDate(LocalDate destructionDate) {
		this.destructionDate = destructionDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
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

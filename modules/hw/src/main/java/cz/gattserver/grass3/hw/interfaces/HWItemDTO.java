package cz.gattserver.grass3.hw.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * HW Objekt
 */
public class HWItemDTO extends HWItemOverviewDTO {

	/**
	 * Typ - klasifikace hw
	 */
	private Set<HWItemTypeDTO> types;

	/**
	 * Datum vyhození, zničení, prodání
	 */
	private LocalDate destructionDate;

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

	public HWItemOverviewDTO getUsedIn() {
		return usedIn;
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

	public Set<HWItemTypeDTO> getTypes() {
		return types;
	}

	public void setTypes(Set<HWItemTypeDTO> types) {
		this.types = types;
	}

	public LocalDate getDestructionDate() {
		return destructionDate;
	}

	public void setDestructionDate(LocalDate destructionDate) {
		this.destructionDate = destructionDate;
	}

	public List<ServiceNoteDTO> getServiceNotes() {
		return serviceNotes;
	}

	public void setServiceNotes(List<ServiceNoteDTO> serviceNotes) {
		this.serviceNotes = serviceNotes;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}

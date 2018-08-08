package cz.gattserver.grass3.hw.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * HW Objekt
 */
public class HWItemTO extends HWItemOverviewTO {

	private static final long serialVersionUID = 4661359528372859703L;

	/**
	 * Typ - klasifikace hw
	 */
	private Set<String> types;

	/**
	 * Datum vyhození, zničení, prodání
	 */
	private LocalDate destructionDate;

	/**
	 * Poznámky ke stavu hw - opravy apod.
	 */
	private List<ServiceNoteTO> serviceNotes;

	/**
	 * Počet let záruky
	 */
	private Integer warrantyYears;

	/**
	 * Součást celku
	 */
	private HWItemOverviewTO usedIn;

	public HWItemOverviewTO getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(HWItemOverviewTO usedIn) {
		this.usedIn = usedIn;
	}

	public Integer getWarrantyYears() {
		return warrantyYears;
	}

	public void setWarrantyYears(Integer warrantyYears) {
		this.warrantyYears = warrantyYears;
	}

	public Set<String> getTypes() {
		return types;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	public LocalDate getDestructionDate() {
		return destructionDate;
	}

	public void setDestructionDate(LocalDate destructionDate) {
		this.destructionDate = destructionDate;
	}

	public List<ServiceNoteTO> getServiceNotes() {
		return serviceNotes;
	}

	public void setServiceNotes(List<ServiceNoteTO> serviceNotes) {
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

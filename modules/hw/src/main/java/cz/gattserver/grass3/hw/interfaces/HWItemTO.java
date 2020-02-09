package cz.gattserver.grass3.hw.interfaces;

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

	/**
	 * Popis
	 */
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

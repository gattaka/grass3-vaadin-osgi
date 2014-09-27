package cz.gattserver.grass3.hw.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Typ hw
 */
public class HWItemTypeDTO {

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

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof HWItemTypeDTO) {
			return ((HWItemTypeDTO) obj).getName().equals(name);
		} else
			return false;
	}

}

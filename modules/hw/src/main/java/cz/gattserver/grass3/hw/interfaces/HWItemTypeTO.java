package cz.gattserver.grass3.hw.interfaces;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Typ hw
 */
public class HWItemTypeTO {

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
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof HWItemTypeTO) {
			return id.equals(((HWItemTypeTO) obj).getId());
		} else
			return false;
	}

}

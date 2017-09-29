package cz.gattserver.grass3.model.dto;

public class NodeOverviewDTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název uzlu
	 */
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

}

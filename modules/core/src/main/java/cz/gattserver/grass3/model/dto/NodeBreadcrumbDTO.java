package cz.gattserver.grass3.model.dto;

public class NodeBreadcrumbDTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název uzlu
	 */
	private String name;

	/**
	 * Předek uzlu
	 */
	private NodeBreadcrumbDTO parent;

	public NodeBreadcrumbDTO getParent() {
		return parent;
	}

	public void setParent(NodeBreadcrumbDTO parent) {
		this.parent = parent;
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

}

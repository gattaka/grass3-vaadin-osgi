package cz.gattserver.grass3.model.dto;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentTagOverviewDTO implements Comparable<ContentTagOverviewDTO> {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Počet obsahů k tagu
	 */
	private int contentNodesCount;

	public ContentTagOverviewDTO() {
	}

	public ContentTagOverviewDTO(String name) {
		this.name = name;
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

	public int getContentNodesCount() {
		return contentNodesCount;
	}

	public void setContentNodesCount(int contentNodesCount) {
		this.contentNodesCount = contentNodesCount;
	}

	@Override
	public int compareTo(ContentTagOverviewDTO o) {
		return getName().compareTo(o.getName());
	}

}

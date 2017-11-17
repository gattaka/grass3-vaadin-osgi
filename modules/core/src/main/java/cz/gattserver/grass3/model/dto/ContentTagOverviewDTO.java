package cz.gattserver.grass3.model.dto;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentTagOverviewDTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název tagu
	 */
	private String name;

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

}

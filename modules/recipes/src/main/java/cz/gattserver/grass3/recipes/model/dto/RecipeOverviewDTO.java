package cz.gattserver.grass3.recipes.model.dto;

public class RecipeOverviewDTO {

	/**
	 * Název receptu
	 */
	private String name;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public RecipeOverviewDTO() {
	}

	public RecipeOverviewDTO(Long id, String name) {
		super();
		this.id = id;
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

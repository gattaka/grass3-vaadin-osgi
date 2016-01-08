package cz.gattserver.grass3.recipes.model.dto;

public class RecipeDTO {

	/**
	 * Popis receptu
	 */
	private String description;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

package cz.gattserver.grass3.recipes.facades;

import java.util.List;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;

public interface IRecipeFacade {

	/**
	 * Získá všechny recepty
	 */
	public List<RecipeDTO> getRecipes();

	/**
	 * Získá recept dle id
	 */
	public RecipeDTO getRecipeById(Long id);

	/**
	 * Založí nový recept
	 */
	public boolean createNewRecipe(String name, String desc);

}

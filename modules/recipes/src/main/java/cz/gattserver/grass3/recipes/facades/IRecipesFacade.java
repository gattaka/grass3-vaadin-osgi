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
	public Long saveRecipe(String name, String desc, Long id);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak 
	 */
	public String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >" 
	 */
	public String eolToBreakline(String text);

}

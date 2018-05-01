package cz.gattserver.grass3.recipes.facades;

import java.util.List;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewDTO;

public interface RecipesFacade {

	/**
	 * Získá počet receptů v DB
	 */
	public int getRecipesCount();

	/**
	 * Získá všechny recepty pro REST použití
	 * 
	 * @param page
	 *            číslo stránky
	 * @param pageSize
	 *            velikost stránky
	 */
	public List<RecipeOverviewDTO> getRecipesForREST(int page, int pageSize);

	/**
	 * Získá všechny recepty
	 */
	public List<RecipeOverviewDTO> getRecipes();

	/**
	 * Získá recept dle id
	 */
	public RecipeDTO getRecipeById(Long id);

	/**
	 * Založí/uprav nový recept
	 */
	public Long saveRecipe(String name, String desc, Long id);

	public Long saveRecipe(String name, String desc);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	public String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	public String eolToBreakline(String text);

}

package cz.gattserver.grass3.recipes.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.recipes.model.domain.Recipe;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená, že před tímto mapperem nedošlo k
 * uzavření session, ve které byl původní objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během mapování tak může docházet k dotazům na
 * DB, které produkují tyto proxy objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("recipeMapper")
public class Mapper {

	/**
	 * Převede {@link Recipe} na {@link RecipeDTO}
	 * 
	 * @param e
	 * @return
	 */
	public RecipeDTO mapRecipe(Recipe e) {
		if (e == null)
			return null;

		RecipeDTO recipeDTO = new RecipeDTO();

		recipeDTO.setId(e.getId());
		recipeDTO.setDescription(e.getDescription());

		return recipeDTO;
	}

	/**
	 * Převede list {@link Recipe} na list {@link RecipeDTO}
	 * 
	 * @param recipes
	 * @return
	 */
	public List<RecipeDTO> mapRecipes(Collection<Recipe> recipes) {
		if (recipes == null)
			return null;

		List<RecipeDTO> recipeDTOs = new ArrayList<RecipeDTO>();
		for (Recipe recipe : recipes) {
			recipeDTOs.add(mapRecipe(recipe));
		}
		return recipeDTOs;
	}
}
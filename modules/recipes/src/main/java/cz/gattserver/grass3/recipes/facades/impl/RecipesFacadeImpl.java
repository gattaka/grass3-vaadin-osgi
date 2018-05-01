package cz.gattserver.grass3.recipes.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.recipes.facades.RecipesFacade;
import cz.gattserver.grass3.recipes.model.dao.RecipeRepository;
import cz.gattserver.grass3.recipes.model.domain.Recipe;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewDTO;
import cz.gattserver.grass3.recipes.util.Mapper;

@Transactional
@Component
public class RecipesFacadeImpl implements RecipesFacade {

	@Autowired
	private Mapper mapper;

	@Autowired
	private RecipeRepository recipeRepository;

	public List<RecipeOverviewDTO> getRecipes() {
		List<Recipe> recipes = recipeRepository.findAllOrderByName();
		if (recipes == null)
			return null;
		List<RecipeOverviewDTO> recipeDTOs = mapper.mapRecipes(recipes);
		return recipeDTOs;
	}

	public RecipeDTO getRecipeById(Long id) {
		Recipe recipe = recipeRepository.findOne(id);
		if (recipe == null)
			return null;
		RecipeDTO recipeDTO = mapper.mapRecipe(recipe);
		return recipeDTO;
	}

	public Long saveRecipe(String name, String desc) {
		return saveRecipe(name, desc, null);
	}

	public Long saveRecipe(String name, String desc, Long id) {
		Recipe recipe = new Recipe();
		recipe.setId(id);
		recipe.setName(name);
		recipe.setDescription(eolToBreakline(desc));
		return recipeRepository.save(recipe).getId();
	}

	public String breaklineToEol(String text) {
		String result = text.replace("<br/>", "" + '\n').replace("<br>", "" + '\n');
		return result;
	}

	public String eolToBreakline(String text) {
		String result = text.replace("" + '\n', "<br/>");
		return result;
	}

	@Override
	public int getRecipesCount() {
		return (int) recipeRepository.count();
	}

	@Override
	public List<RecipeOverviewDTO> getRecipesForREST(int page, int pageSize) {
		return mapper.mapRecipes(recipeRepository.findAllOrderByNamePageable(new PageRequest(page, pageSize)));
	}
}

package cz.gattserver.grass3.recipes.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.recipes.facades.RecipesService;
import cz.gattserver.grass3.recipes.model.dao.RecipeRepository;
import cz.gattserver.grass3.recipes.model.domain.Recipe;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewTO;
import cz.gattserver.grass3.recipes.util.Mapper;

@Transactional
@Component
public class RecipesServiceImpl implements RecipesService {

	@Autowired
	private Mapper mapper;

	@Autowired
	private RecipeRepository recipeRepository;

	@Override
	public RecipeDTO getRecipeById(Long id) {
		Recipe recipe = recipeRepository.findById(id).orElse(null);
		if (recipe == null)
			return null;
		RecipeDTO recipeDTO = mapper.mapRecipe(recipe);
		return recipeDTO;
	}

	@Override
	public Long saveRecipe(String name, String desc) {
		return saveRecipe(name, desc, null);
	}

	@Override
	public Long saveRecipe(String name, String desc, Long id) {
		Recipe recipe = new Recipe();
		recipe.setId(id);
		recipe.setName(name);
		recipe.setDescription(eolToBreakline(desc));
		return recipeRepository.save(recipe).getId();
	}

	@Override
	public String breaklineToEol(String text) {
		String result = text.replace("<br/>", "" + '\n').replace("<br>", "" + '\n');
		return result;
	}

	@Override
	public String eolToBreakline(String text) {
		String result = text.replace("" + '\n', "<br/>");
		return result;
	}

	@Override
	public int getRecipesCount(String filter) {
		return (int) recipeRepository.count(QuerydslUtil.transformSimpleLikeFilter(filter));
	}

	@Override
	public List<RecipeOverviewTO> getRecipes(String filter, Pageable pageable) {
		return mapper.mapRecipes(
				recipeRepository.findAllOrderByNamePageable(QuerydslUtil.transformSimpleLikeFilter(filter), pageable));
	}

}

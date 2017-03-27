package cz.gattserver.grass3.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.recipes.facades.IRecipesFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewDTO;

@Controller
@RequestMapping("/recipes")
public class RecipesResource {

	@Autowired
	private IRecipesFacade recipesFacade;

	@RequestMapping("/list")
	public @ResponseBody List<RecipeOverviewDTO> list() {
		return recipesFacade.getRecipes();
	}

	@RequestMapping("/recipe")
	public @ResponseBody RecipeDTO recipe(@RequestParam(value = "id", required = true) Long id) {
		return recipesFacade.getRecipeById(id);
	}

}

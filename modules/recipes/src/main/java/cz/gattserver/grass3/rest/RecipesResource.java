package cz.gattserver.grass3.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.recipes.facades.RecipesFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewDTO;

@Controller
@RequestMapping("/recipes")
public class RecipesResource {

	@Autowired
	private RecipesFacade recipesFacade;

	@RequestMapping("/list")
	public ResponseEntity<List<RecipeOverviewDTO>> list(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		int count = recipesFacade.getRecipesCount();
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(recipesFacade.getRecipesForREST(page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/count")
	public ResponseEntity<Integer> count() {
		return new ResponseEntity<>(recipesFacade.getRecipesCount(), HttpStatus.OK);
	}

	@RequestMapping("/recipe")
	public @ResponseBody RecipeDTO recipe(@RequestParam(value = "id", required = true) Long id) {
		return recipesFacade.getRecipeById(id);
	}

}

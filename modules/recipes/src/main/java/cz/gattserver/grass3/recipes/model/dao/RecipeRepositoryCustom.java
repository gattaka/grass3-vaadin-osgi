package cz.gattserver.grass3.recipes.model.dao;

import java.util.List;

import cz.gattserver.grass3.recipes.model.domain.Recipe;

public interface RecipeRepositoryCustom {

	int count(String filter);

	List<Recipe> fetch(String filter, int offset, int limit);
}

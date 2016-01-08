package cz.gattserver.grass3.recipes.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.recipes.model.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}

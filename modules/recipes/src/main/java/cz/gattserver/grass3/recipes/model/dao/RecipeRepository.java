package cz.gattserver.grass3.recipes.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.recipes.model.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	@Query("select r from Recipe r order by name asc")
	List<Recipe> findAllSortByName();
}

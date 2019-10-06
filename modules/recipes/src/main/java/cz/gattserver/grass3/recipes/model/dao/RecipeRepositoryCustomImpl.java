package cz.gattserver.grass3.recipes.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.model.util.PredicateBuilder;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.recipes.model.domain.Recipe;

@Repository
public class RecipeRepositoryCustomImpl implements RecipeRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate c(String name) {
		QRecipe r = QRecipe.recipe;
		PredicateBuilder builder = new PredicateBuilder();
		if (name != null)
			builder.iLike(r.name, name);
		return builder.getBuilder();
	}

	@Override
	public int count(String name) {
		JPAQuery<Recipe> query = new JPAQuery<>(entityManager);
		QRecipe r = QRecipe.recipe;
		return query.from(b).where(name(name)).fetchCount();
	}

	@Override
	public List<Recipe> fetch(String name, int offset, int limit) {
		JPAQuery<Recipe> query = new JPAQuery<>(entityManager);
		QRecipe r = QRecipe.recipe;
		return query.from(b).where(createPredicate(name)).orderBy(r.name).fetch();
	}

}

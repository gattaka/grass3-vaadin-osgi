package cz.gattserver.grass3.drinks.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass3.drinks.model.domain.Drink;
import cz.gattserver.grass3.drinks.model.domain.QDrink;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkOverviewTO;
import cz.gattserver.grass3.model.util.PredicateBuilder;

@Repository
public class DrinkRepositoryCustomImpl implements DrinkRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Drink> findAllOrderByName(DrinkOverviewTO filterTO) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QDrink s = QDrink.drink;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(s.name, filterTO.getName());
		return query.select(s).from(s).where(builder.getBuilder()).orderBy(new OrderSpecifier<>(Order.ASC, s.name))
				.fetch();
	}
}

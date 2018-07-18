package cz.gattserver.grass3.drinks.model.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.drinks.model.domain.Drink;


public interface DrinkRepository extends JpaRepository<Drink, Long>, DrinkRepositoryCustom {

	@Query("select d from DRINK d order by name asc")
	List<Drink> findAllOrderByNamePageable(Pageable pageRequest);

}

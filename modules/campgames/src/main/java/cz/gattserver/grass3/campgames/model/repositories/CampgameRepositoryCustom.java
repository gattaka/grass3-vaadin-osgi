package cz.gattserver.grass3.campgames.model.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass3.campgames.model.domain.Campgame;

public interface CampgameRepositoryCustom {

	long countCampgames(CampgameFilterTO filter);

	List<Campgame> getCampgames(CampgameFilterTO filter, Pageable pageable, OrderSpecifier<?>[] order);
}

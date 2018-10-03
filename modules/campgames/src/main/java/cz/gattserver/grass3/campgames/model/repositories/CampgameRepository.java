package cz.gattserver.grass3.campgames.model.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.campgames.model.domain.Campgame;

public interface CampgameRepository extends JpaRepository<Campgame, Long>, CampgameRepositoryCustom {

	public List<Campgame> findByKeywordsId(Long id);

	@Query("select i from CAMPGAME i inner join i.keywords keywords where keywords.name in ?1")
	public List<Campgame> getCampgamesByKeywords(Collection<String> keywords);

}

package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.songs.model.domain.Chord;

public interface ChordsRepository extends JpaRepository<Chord, Long>, ChordsRepositoryCustom {

	@Query("select s from CHORD s order by instrument asc, name asc")
	List<Chord> findAllOrderByNamePageable(Pageable pageRequest);

}

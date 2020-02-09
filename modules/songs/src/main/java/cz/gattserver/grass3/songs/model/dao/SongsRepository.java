package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.songs.model.domain.Song;

public interface SongsRepository extends JpaRepository<Song, Long>, SongsRepositoryCustom {

	@Query("select s from SONG s order by name asc")
	List<Song> findAllOrderByNamePageable(Pageable pageRequest);

}

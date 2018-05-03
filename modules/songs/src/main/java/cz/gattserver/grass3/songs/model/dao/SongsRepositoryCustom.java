package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import cz.gattserver.grass3.songs.model.domain.Song;
import cz.gattserver.grass3.songs.model.dto.SongOverviewTO;

public interface SongsRepositoryCustom {

	List<Song> findAllOrderByName(SongOverviewTO filterTO);
}

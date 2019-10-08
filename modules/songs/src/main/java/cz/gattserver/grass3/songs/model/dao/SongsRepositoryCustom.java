package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;

public interface SongsRepositoryCustom {

	List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO, int offset, int limit);

	long count(SongOverviewTO filterTO);
}

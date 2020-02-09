package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;

public interface SongsRepositoryCustom {

	long count(SongOverviewTO filterTO);

	List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO, int offset, int limit);

	List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO);

}

package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;

public interface SongsRepositoryCustom {

	List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO, Pageable pageable);

	long count(SongOverviewTO filterTO);
}

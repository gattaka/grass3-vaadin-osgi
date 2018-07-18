package cz.gattserver.grass3.songs.model.dao;

import java.util.List;

import cz.gattserver.grass3.songs.model.domain.Chord;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;

public interface ChordsRepositoryCustom {

	List<Chord> findAllOrderByName(ChordTO filterTO);
}

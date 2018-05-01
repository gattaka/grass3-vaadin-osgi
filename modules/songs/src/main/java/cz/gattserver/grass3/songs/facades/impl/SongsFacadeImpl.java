package cz.gattserver.grass3.songs.facades.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.dao.SongsRepository;
import cz.gattserver.grass3.songs.model.domain.Song;
import cz.gattserver.grass3.songs.model.dto.SongDTO;
import cz.gattserver.grass3.songs.model.dto.SongOverviewDTO;
import cz.gattserver.grass3.songs.util.Mapper;

@Transactional
@Component
public class SongsFacadeImpl implements SongsFacade {

	@Autowired
	private Mapper mapper;

	@Autowired
	private SongsRepository songsRepository;

	public List<SongOverviewDTO> getSongs() {
		List<Song> songs = songsRepository.findAllOrderByName();
		if (songs == null)
			return null;
		return mapper.mapSongs(songs);
	}

	public SongDTO getSongById(Long id) {
		Song song = songsRepository.findOne(id);
		if (song == null)
			return null;
		return mapper.mapSong(song);
	}

	public Long saveSong(SongDTO to) {
		Song song = mapper.mapSong(to);
		song.setText(eolToBreakline(to.getText()));
		return songsRepository.save(song).getId();
	}

	public String breaklineToEol(String text) {
		return text.replace("<br/>", "" + '\n').replace("<br>", "" + '\n');
	}

	public String eolToBreakline(String text) {
		return text.replace("" + '\n', "<br/>");
	}

	@Override
	public int getSongsCount() {
		return (int) songsRepository.count();
	}

	@Override
	public List<SongOverviewDTO> getSongsForREST(int page, int pageSize) {
		return mapper.mapSongs(songsRepository.findAllOrderByNamePageable(new PageRequest(page, pageSize)));
	}
}

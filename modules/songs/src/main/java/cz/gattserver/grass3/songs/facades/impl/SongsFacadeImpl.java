package cz.gattserver.grass3.songs.facades.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.dao.ChordsRepository;
import cz.gattserver.grass3.songs.model.dao.SongsRepository;
import cz.gattserver.grass3.songs.model.domain.Chord;
import cz.gattserver.grass3.songs.model.domain.Song;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.songs.util.Mapper;

@Transactional
@Component
public class SongsFacadeImpl implements SongsFacade {

	@Autowired
	private Mapper mapper;

	@Autowired
	private SongsRepository songsRepository;

	@Autowired
	private ChordsRepository chordsRepository;

	@Override
	public List<SongOverviewTO> getSongs(SongOverviewTO filterTO) {
		List<Song> songs = songsRepository.findAllOrderByName(filterTO);
		if (songs == null)
			return null;
		return mapper.mapSongs(songs);
	}

	@Override
	public SongTO getSongById(Long id) {
		Song song = songsRepository.findOne(id);
		if (song == null)
			return null;
		return mapper.mapSong(song);
	}

	@Override
	public SongTO saveSong(SongTO to) {
		Song song = mapper.mapSong(to);
		song.setText(eolToBreakline(to.getText()));
		song = songsRepository.save(song);
		return mapper.mapSong(song);
	}

	@Override
	public String breaklineToEol(String text) {
		return text.replace("<br/>", "" + '\n').replace("<br>", "" + '\n');
	}

	@Override
	public String eolToBreakline(String text) {
		text = text.replace("\r\n", "<br/>");
		return text.replace("\n", "<br/>");
	}

	@Override
	public int getSongsCount() {
		return (int) songsRepository.count();
	}

	@Override
	public List<SongOverviewTO> getSongsForREST(int page, int pageSize) {
		return mapper.mapSongs(songsRepository.findAllOrderByNamePageable(new PageRequest(page, pageSize)));
	}

	@Override
	public void deleteSong(Long id) {
		songsRepository.delete(id);
	}

	@Override
	public SongTO importSong(String author, InputStream in, String fileName, String mime, long size,
			int filesLeftInQueue) {
		SongTO to = new SongTO();
		// odřízne příponu
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		int nameEnd = fileName.indexOf("(");
		int year = 0;
		if (nameEnd < 0)
			nameEnd = fileName.length();
		else {
			String yearPart = fileName.substring(nameEnd);
			for (String chunk : yearPart.split(" |\\)|\\(")) {
				if (chunk.matches("[0-9]+")) {
					try {
						year = Integer.parseInt(chunk);
					} catch (NumberFormatException e) {
						// nezdařilo se naparsovat číslo... ?
					}
					break;
				}
			}
		}
		to.setYear(year);
		to.setName(fileName.substring(0, nameEnd).trim());
		to.setAuthor(author);
		try {
			to.setText(Streams.asString(in, "cp1250"));
		} catch (IOException e) {
			to.setText("Nezdařilo se zpracovat obsah souboru");
		}
		return saveSong(to);
	}

	@Override
	public ChordTO saveChord(ChordTO to) {
		Chord chord = mapper.mapChord(to);
		chord = chordsRepository.save(chord);
		to.setId(chord.getId());
		return to;
	}

	@Override
	public void deleteChord(Long id) {
		chordsRepository.delete(id);
	}

	@Override
	public List<ChordTO> getChords(ChordTO filterTO) {
		return mapper.mapChords(chordsRepository.findAllOrderByName(filterTO));
	}

	@Override
	public ChordTO getChordById(Long id) {
		return mapper.mapChord(chordsRepository.findOne(id));
	}

	@Override
	public ChordTO getChordByName(String name) {
		return mapper.mapChord(chordsRepository.findByName(name));
	}
}

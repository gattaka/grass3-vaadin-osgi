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

	public SongDTO saveSong(SongDTO to) {
		Song song = mapper.mapSong(to);
		song.setText(eolToBreakline(to.getText()));
		song = songsRepository.save(song);
		return mapper.mapSong(song);
	}

	public String breaklineToEol(String text) {
		return text.replace("<br/>", "" + '\n').replace("<br>", "" + '\n');
	}

	public String eolToBreakline(String text) {
		text = text.replace("\r\n", "<br/>");
		return text.replace("\n", "<br/>");
	}

	@Override
	public int getSongsCount() {
		return (int) songsRepository.count();
	}

	@Override
	public List<SongOverviewDTO> getSongsForREST(int page, int pageSize) {
		return mapper.mapSongs(songsRepository.findAllOrderByNamePageable(new PageRequest(page, pageSize)));
	}

	@Override
	public void deleteSong(Long id) {
		songsRepository.delete(id);
	}

	@Override
	public SongDTO importSong(String author, InputStream in, String fileName, String mime, long size,
			int filesLeftInQueue) {
		SongDTO to = new SongDTO();
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
}

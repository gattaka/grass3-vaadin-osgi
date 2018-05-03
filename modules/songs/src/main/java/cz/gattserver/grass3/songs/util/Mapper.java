package cz.gattserver.grass3.songs.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.songs.model.domain.Song;
import cz.gattserver.grass3.songs.model.dto.SongTO;
import cz.gattserver.grass3.songs.model.dto.SongOverviewTO;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená, že před tímto mapperem nedošlo k
 * uzavření session, ve které byl původní objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během mapování tak může docházet k dotazům na
 * DB, které produkují tyto proxy objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("songsMapper")
public class Mapper {

	/**
	 * Převede {@link Song} na {@link SongTO}
	 * 
	 * @param e
	 * @return
	 */
	public SongTO mapSong(Song e) {
		if (e == null)
			return null;

		SongTO song = new SongTO();

		song.setId(e.getId());
		song.setName(e.getName());
		song.setAuthor(e.getAuthor());
		song.setYear(e.getYear());
		song.setText(e.getText());

		return song;
	}

	/**
	 * Převede list {@link Song} na list {@link SongTO}
	 * 
	 * @param songs
	 * @return
	 */
	public List<SongOverviewTO> mapSongs(Collection<Song> songs) {
		if (songs == null)
			return null;

		List<SongOverviewTO> songsDTOs = new ArrayList<SongOverviewTO>();
		for (Song song : songs) {
			songsDTOs.add(new SongOverviewTO(song.getName(), song.getAuthor(), song.getYear(), song.getId()));
		}
		return songsDTOs;
	}

	/**
	 * Převede {@link SongTO} na {@link Song}
	 * 
	 * @param e
	 * @return
	 */
	public Song mapSong(SongTO e) {
		if (e == null)
			return null;

		Song song = new Song();

		song.setId(e.getId());
		song.setName(e.getName());
		song.setAuthor(e.getAuthor());
		song.setYear(e.getYear());
		song.setText(e.getText());

		return song;
	}

}
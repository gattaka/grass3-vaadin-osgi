package cz.gattserver.grass3.songs.facades;

import java.io.InputStream;
import java.util.List;

import cz.gattserver.grass3.songs.model.dto.SongTO;
import cz.gattserver.grass3.songs.model.dto.ChordTO;
import cz.gattserver.grass3.songs.model.dto.SongOverviewTO;

public interface SongsFacade {

	/**
	 * Získá počet písniček v DB
	 */
	public int getSongsCount();

	/**
	 * Získá všechny písničky pro REST použití
	 * 
	 * @param page
	 *            číslo stránky
	 * @param pageSize
	 *            velikost stránky
	 */
	public List<SongOverviewTO> getSongsForREST(int page, int pageSize);

	/**
	 * Získá všechny písničky
	 * 
	 * @param filterTO
	 */
	public List<SongOverviewTO> getSongs(SongOverviewTO filterTO);

	/**
	 * Získá písničku dle id
	 */
	public SongTO getSongById(Long id);

	/**
	 * Založí/uprav novou písničku
	 */
	public SongTO saveSong(SongTO songDTO);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	public String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	public String eolToBreakline(String text);

	/**
	 * Smaže písničku
	 * 
	 * @param id
	 */
	public void deleteSong(Long id);

	/**
	 * Provede import písničky ze souboru
	 * 
	 * @param author
	 * @param in
	 * @param fileName
	 * @param mime
	 * @param size
	 * @param filesLeftInQueue
	 * @return
	 */
	public SongTO importSong(String author, InputStream in, String fileName, String mime, long size,
			int filesLeftInQueue);

	/**
	 * Uloží akord
	 * 
	 * @param to
	 *            akord
	 * @return
	 */
	public ChordTO saveChord(ChordTO to);

	/**
	 * Smaže akord
	 * 
	 * @param id
	 */
	public void deleteChord(Long id);

	/**
	 * Vyhledá akordy dle filtru
	 * 
	 * @param filterTO
	 * @return
	 */
	public List<ChordTO> getChords(ChordTO filterTO);

	/**
	 * Vyhledá akord dle id
	 * 
	 * @param id
	 * @return
	 */
	public ChordTO getChordById(Long id);

}

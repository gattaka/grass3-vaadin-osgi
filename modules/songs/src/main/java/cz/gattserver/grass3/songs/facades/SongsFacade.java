package cz.gattserver.grass3.songs.facades;

import java.util.List;

import cz.gattserver.grass3.songs.model.dto.SongDTO;
import cz.gattserver.grass3.songs.model.dto.SongOverviewDTO;

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
	public List<SongOverviewDTO> getSongsForREST(int page, int pageSize);

	/**
	 * Získá všechny písničky
	 */
	public List<SongOverviewDTO> getSongs();

	/**
	 * Získá písničku dle id
	 */
	public SongDTO getSongById(Long id);

	/**
	 * Založí/uprav novou písničku
	 */
	public Long saveSong(SongDTO songDTO);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	public String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	public String eolToBreakline(String text);

}

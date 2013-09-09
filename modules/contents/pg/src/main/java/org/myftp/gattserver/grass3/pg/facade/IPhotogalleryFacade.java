package org.myftp.gattserver.grass3.pg.facade;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;

public interface IPhotogalleryFacade {

	/**
	 * Smaže galerii
	 * 
	 * @param photogallery
	 *            galerie ke smazání
	 * @return {@code true} pokud se zdařilo smazat jinak {@code false}
	 */
	public boolean deletePhotogallery(PhotogalleryDTO photogallery);

	/**
	 * Upraví galerii
	 * 
	 * @param name
	 *            název galerie
	 * @param tags
	 *            klíčová slova galerie
	 * @param publicated
	 *            je galerie publikována ?
	 * @param photogallery
	 *            původní galerie
	 * @return {@code true} pokud se úprava zdařila, jinak {@code false}
	 */
	public boolean modifyPhotogallery(String name, Collection<String> tags,
			boolean publicated, PhotogalleryDTO photogallery, String contextRoot);

	/**
	 * Uloží galerii
	 * 
	 * @param name
	 *            název galerie
	 * @param tags
	 *            klíčová slova galerie
	 * @param galleryDir
	 *            adresář se soubory fotogalerie
	 * @param publicated
	 *            je galerie publikována ?
	 * @param category
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který galerii vytvořil
	 * @return identifikátor galerie pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long savePhotogallery(String name, Collection<String> tags,
			File galleryDir, boolean publicated, NodeDTO category,
			UserInfoDTO author, String contextRoot);

	/**
	 * Získá galerii dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO galerie
	 */
	public PhotogalleryDTO getPhotogalleryForDetail(Long id);

	/**
	 * Získá všechny galerie pro přehled
	 */
	public List<PhotogalleryDTO> getAllPhotogalleriesForOverview();

	/**
	 * Získá všechny galerie a namapuje je pro použití při vyhledávání
	 */
	public List<PhotogalleryDTO> getAllPhotogalleriesForSearch();

	/**
	 * Vytvoří nový adresář pro fotogalerii
	 */
	public File createGalleryDir();

	/**
	 * Získá objekt konfigurace
	 */
	public PhotogalleryConfiguration getConfiguration();

	/**
	 * Získá adresář galerie
	 * 
	 * @param photogallery
	 *            objekt galerie
	 * @return adresář
	 */
	public File getGalleryDir(PhotogalleryDTO photogallery);

	/**
	 * Pokusí se smazat miniaturu od předaného souboru
	 * 
	 * @param file
	 *            soubor fotografie
	 * @param photogalleryDTO
	 *            objekt galerie
	 */
	public void tryDeleteMiniature(File file, PhotogalleryDTO photogalleryDTO);

}

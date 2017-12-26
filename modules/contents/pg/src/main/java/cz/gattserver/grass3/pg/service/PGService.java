package cz.gattserver.grass3.pg.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.exception.UnauthorizedAccessException;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;

public interface PGService {

	/**
	 * Smaže galerii
	 * 
	 * @param photogallery
	 *            galerie ke smazání
	 * @return {@code true} pokud se zdařilo smazat jinak {@code false}
	 */
	public void deletePhotogallery(long photogalleryId);

	/**
	 * Upraví galerii
	 * 
	 * @param photogalleryId
	 *            id původní galerie
	 * @param payloadTO
	 *            obsah galerie
	 * @param date
	 */
	public void modifyPhotogallery(long photogalleryId, PhotogalleryPayloadTO payloadTO, LocalDateTime date);

	/**
	 * Uloží galerii
	 * 
	 * @param payloadTO
	 *            obsah galerie
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který galerii vytvořil
	 * @param date
	 */
	public void savePhotogallery(PhotogalleryPayloadTO payloadTO, long nodeId, long authorId, LocalDateTime date);

	/**
	 * Získá galerii dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return TO galerie
	 */
	public PhotogalleryTO getPhotogalleryForDetail(long id);

	/**
	 * Získá všechny galerie a namapuje je pro použití při vyhledávání
	 */
	public List<PhotogalleryTO> getAllPhotogalleriesForSearch();

	/**
	 * Vytvoří nový adresář pro fotogalerii
	 */
	public Path createGalleryDir();

	/**
	 * Získá objekt konfigurace
	 */
	public PGConfiguration getConfiguration();

	/**
	 * Získá adresář galerie
	 * 
	 * @param photogallery
	 *            objekt galerie
	 * @return adresář
	 */
	public Path getGalleryDir(PhotogalleryTO photogallery);

	/**
	 * Pokusí se smazat miniaturu od předaného souboru
	 * 
	 * @param file
	 *            soubor fotografie
	 * @param photogalleryDTO
	 *            objekt galerie
	 */
	public void tryDeleteMiniatureImage(String file, PhotogalleryTO photogalleryDTO);

	/**
	 * Uloží konfiguraci
	 */
	public void storeConfiguration(PGConfiguration configuration);

	/**
	 * Pokusí se smazat slideshow verzi od předaného souboru
	 * 
	 * @param file
	 *            soubor fotografie
	 * @param photogalleryDTO
	 *            objekt galerie
	 */
	public void tryDeleteSlideshowImage(String file, PhotogalleryTO photogalleryDTO);

	/**
	 * Pokusí se smazat preview verzi od předaného videa
	 * 
	 * @param file
	 *            soubor videa
	 * @param photogalleryDTO
	 *            objekt galerie
	 */
	void tryDeletePreviewImage(String file, PhotogalleryTO photogalleryDTO);

	/**
	 * Získá všechny galerie a namapuje je pro použití REST
	 */
	public List<PhotogalleryRESTOverviewTO> getAllPhotogalleriesForREST(Long userId);

	/**
	 * Získá detail fotogalerie pro REST
	 * 
	 * @param id
	 *            idetifikátor galerie
	 * @return
	 */
	public PhotogalleryRESTTO getPhotogalleryForREST(Long id) throws UnauthorizedAccessException;

	/**
	 * Získá fotografii dle galerie pro REST
	 * 
	 * @param id
	 *            idetifikátor galerie
	 * @param fileName
	 *            jméno fotografie
	 * @param mini
	 *            jde miniaturu nebo plnou velikost?
	 * @return
	 */
	public Path getPhotoForREST(Long id, String fileName, boolean mini) throws UnauthorizedAccessException;

	/**
	 * Zazipuje galerii
	 * 
	 * @param galleryDir
	 */
	public void zipGallery(Path galleryDir);

}

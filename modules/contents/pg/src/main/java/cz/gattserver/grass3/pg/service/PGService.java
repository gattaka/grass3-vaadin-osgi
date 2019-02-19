package cz.gattserver.grass3.pg.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass3.pg.exception.UnauthorizedAccessException;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotoVersion;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;

public interface PGService {

	/**
	 * Smaže galerii
	 * 
	 * @param photogallery
	 *            galerie ke smazání
	 * @return {@code true} pokud se zdařilo smazat jinak {@code false}
	 * @throws IOException
	 */
	public void deletePhotogallery(long photogalleryId) throws IOException;

	/**
	 * Upraví galerii. Zpracování je prováděno v samostatném vlákně. Pro
	 * komunikaci zpět jsou použity událost {@link PGProcessStartEvent},
	 * {@link PGProcessProgressEvent} a {@link PGProcessResultEvent}
	 * 
	 * @param photogalleryId
	 *            id původní galerie
	 * @param payloadTO
	 *            obsah galerie
	 * @param date
	 *            datum vytvoření galerie, může být i <code>null</code>, pak
	 *            bude použito aktuální datum
	 */
	public void modifyPhotogallery(long photogalleryId, PhotogalleryPayloadTO payloadTO, LocalDateTime date);

	/**
	 * Uloží galerii. Zpracování je prováděno v samostatném vlákně. Pro
	 * komunikaci zpět jsou použity událost {@link PGProcessStartEvent},
	 * {@link PGProcessProgressEvent} a {@link PGProcessResultEvent}
	 * 
	 * @param payloadTO
	 *            obsah galerie
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který galerii vytvořil
	 * @param date
	 *            datum vytvoření galerie, může být i <code>null</code>, pak
	 *            bude použito aktuální datum
	 */
	public void savePhotogallery(PhotogalleryPayloadTO payloadTO, long nodeId, long authorId, LocalDateTime date);

	/**
	 * Získá galerii dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return TO galerie
	 */
	public PhotogalleryTO getPhotogalleryForDetail(Long id);

	/**
	 * Získá všechny galerie a namapuje je pro použití při vyhledávání
	 */
	public List<PhotogalleryTO> getAllPhotogalleriesForSearch();

	/**
	 * Vytvoří nový adresář pro fotogalerii
	 * 
	 * @throws IOException
	 */
	public String createGalleryDir() throws IOException;

	/**
	 * Získá objekt konfigurace
	 */
	public PGConfiguration loadConfiguration();

	/**
	 * Uloží konfiguraci
	 */
	public void storeConfiguration(PGConfiguration configuration);

	/**
	 * Získá počet galerií pro použití REST
	 * 
	 * @param userId
	 *            id přihlášeného uživatele, může být i <code>null</code>
	 * @param filter
	 *            název galerie (s *)
	 */
	int countAllPhotogalleriesForREST(Long userId, String filter);

	/**
	 * Získá všechny galerie a namapuje je pro použití REST
	 * 
	 * @param userId
	 *            id přihlášeného uživatele, může být i <code>null</code>
	 * @param filter
	 *            název galerie (s *)
	 * @param page
	 *            číslo stránky
	 * @param pageSize
	 *            velikost stránky
	 */
	public List<PhotogalleryRESTOverviewTO> getAllPhotogalleriesForREST(Long userId, String filter, int page,
			int pageSize);

	/**
	 * Získá detail fotogalerie pro REST
	 * 
	 * @param id
	 *            idetifikátor galerie
	 * @return {@link UnauthorizedAccessException}
	 */
	public PhotogalleryRESTTO getPhotogalleryForREST(Long id) throws UnauthorizedAccessException;

	/**
	 * Získá fotografii dle galerie pro REST
	 * 
	 * @param id
	 *            idetifikátor galerie
	 * @param fileName
	 *            jméno fotografie
	 * @param photoVersion
	 *            o jakou velikost fotky jde
	 * @return {@link UnauthorizedAccessException}
	 */
	public Path getPhotoForREST(Long id, String fileName, PhotoVersion mini) throws UnauthorizedAccessException;

	/**
	 * Zazipuje galerii
	 * 
	 * @param galleryDir
	 */
	public void zipGallery(String galleryDir);

	/**
	 * Smaže vybrané soubory z fotogalerie.
	 * 
	 * @param selected
	 *            vybrané soubory
	 * @param galleryDir
	 *            adresář galerie
	 * @return kolekci všech položek, které se podařilo úspěšně smazat
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public List<PhotogalleryViewItemTO> deleteFiles(Set<PhotogalleryViewItemTO> selected, String galleryDir);

	/**
	 * Získá obrázek z galerie. Nemusí jít o existující galerii, proto je
	 * předáván pouze její adresář.
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @param file
	 *            jméno souboru, který má být předáván
	 * @return soubor obrázku
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný soubor podtéká kořen galerie
	 */
	public Path getFullImage(String galleryDir, String file);

	/**
	 * Nahraje soubory do galerie
	 * 
	 * @param in
	 *            vstupní proud dat
	 * @param fileName
	 *            jméno souboru
	 * @param galleryDir
	 *            adresář galerie
	 * @throws IOException
	 *             pokud se nezdařilo uložení souboru
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public void uploadFile(InputStream in, String fileName, String galleryDir) throws IOException;

	/**
	 * Získá list souborů dle galerie
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @return list souborů
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public List<PhotogalleryViewItemTO> getItems(String galleryDir) throws IOException;

	/**
	 * Získá počet položek k zobrazení přehledu (miniatury obrázků a náhledy
	 * videí)
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @return počet položek
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public int getViewItemsCount(String galleryDir) throws IOException;

	/**
	 * Získá položky k zobrazení přehledu (miniatury obrázků a náhledy videí)
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @param skip
	 *            počet přeskočených položek (stránkování)
	 * @param limit
	 *            počet položek (stránkování)
	 * @return položky
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public List<PhotogalleryViewItemTO> getViewItems(String galleryDir, int skip, int limit) throws IOException;

	/**
	 * Získá položku obrázku ze slideshow dle indexu
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @param index
	 *            index obrázku ve slideshow
	 * @return položka obrázku slideshow
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public PhotogalleryViewItemTO getSlideshowItem(String galleryDir, int index) throws IOException;

	/**
	 * Ověří, že galerie existuje a má patřičné podadresáře
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @return <code>true</code>, pokud je galerie v pořádku a připravena k
	 *         zobrazení
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	public boolean checkGallery(String galleryDir);

	/**
	 * Smaže vygenerovaný zip soubor galerie
	 * 
	 * @param zipFile
	 *            zip soubor
	 */
	public void deleteZipFile(Path zipFile);

	/**
	 * Smaže rozpracovanou galerii, která ještě nebyla uložena do DB
	 * 
	 * @param galleryDir
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG adresář galerie
	 */
	public void deleteDraftGallery(String galleryDir) throws IOException;

}

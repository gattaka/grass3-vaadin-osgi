package cz.gattserver.grass3.articles.plugins.favlink;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.plugins.favlink.FaviconUtils;
import cz.gattserver.grass3.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.web.common.spring.SpringContextHelper;

/**
 * Cache již stažených ikon
 * 
 * @author Hynek
 *
 */
public class FaviconCache {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	private Path cacheDir;

	public FaviconCache() {
		SpringContextHelper.inject(this);
		cacheDir = getCacheDirectoryPath();
	}

	public String getFavicon(String pageAddress) {
		URL pageURL = FaviconUtils.getPageURL(pageAddress);
		String faviconRootFilename = FaviconUtils.createFaviconRootFilename(pageURL);

		// Cached ICO
		String faviconICOFilename = faviconRootFilename + ".ico";
		if (Files.exists(cacheDir.resolve(faviconICOFilename)))
			return faviconICOFilename;

		// Cached PNG
		String faviconPNGFilename = faviconRootFilename + ".png";
		if (Files.exists(cacheDir.resolve(faviconPNGFilename)))
			return faviconPNGFilename;

		return null;
	}

	/**
	 * Pokusí se stáhnout a uložit faviconu dle adresy stránky a adresy favicon
	 * souboru a vrátit jméno souboru, pod kterým byla favicona uložena
	 * 
	 * @param pageURL
	 *            adresa stránky jejíž favicon hledám
	 * @param faviconAddress
	 *            adresa souboru favicony
	 * @return název staženého souboru favicony, nebo <code>null</code>, pokud
	 *         se stažení nezdařilo (soubor na adrese neexistuje apod.)
	 */
	public String downloadAndSaveFavicon(URL pageURL, String faviconAddress) {
		String filename = FaviconUtils.getFaviconFilename(pageURL, faviconAddress);
		if (FaviconUtils.downloadFile(cacheDir.resolve(filename), faviconAddress)) {
			return filename;
		} else {
			return null;
		}
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	public Path getCacheDirectoryPath() {
		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);

		// existuje cesta cache?
		Path cacheDir = fileSystemService.getFileSystem().getPath(configuration.getOutputPath());
		if (Files.exists(cacheDir)) {
			if (!Files.isDirectory(cacheDir))
				throw new ParserException("Favicon cache soubor není adresář");
		} else {
			try {
				Files.createDirectories(cacheDir);
			} catch (Exception e) {
				throw new ParserException("Vytváření favicon cache adresáře se nezdařilo", e);
			}
		}

		return cacheDir;
	}

}

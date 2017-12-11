package cz.gattserver.grass3.articles.plugins.favlink.strategies;

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
 * Snaží se získat favicon z cache již stažených ikon
 * 
 * @author Hynek
 *
 */
public abstract class CacheFaviconObtainStrategy implements FaviconObtainStrategy {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	/**
	 * Získá favicon z adresy, uloží ji pod předurčeným názvem + přípona, pod
	 * kterou byla favicona nalezena a vrátí celé jméno uloženého souboru
	 * 
	 * @param pageURL
	 *            stránka, jejíž favicon hledám
	 * @param cacheDir
	 *            {@link Path} adresáře do kterého má být favicon soubor uložen
	 * @param faviconRootFilename
	 *            kořenové jméno souboru, pod kterým bude nalezená favicon
	 *            uložena
	 * @return název souboru -- tedy kořenové jméno + přípona. Pokud se
	 *         favicon nepodařilo najít, pak <code>null</code>
	 */
	protected abstract String onCacheMiss(URL pageURL, Path cacheDir, String faviconRootFilename);

	public CacheFaviconObtainStrategy() {
		SpringContextHelper.inject(this);
	}

	@Override
	public String obtainFaviconURL(String pageAddress, String contextRoot) {
		URL pageURL = FaviconUtils.getPageURL(pageAddress);
		String faviconRootFilename = FaviconUtils.createFaviconRootFilename(pageURL);
		Path cacheDir = getCacheDirectoryPath();

		// Cached ICO
		String faviconICOFilename = faviconRootFilename + ".ico";
		if (Files.exists(cacheDir.resolve(faviconICOFilename)))
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconICOFilename);

		// Cached PNG
		String faviconPNGFilename = faviconRootFilename + ".png";
		if (Files.exists(cacheDir.resolve(faviconPNGFilename)))
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconPNGFilename);

		// Custom strategie
		String fileName = onCacheMiss(pageURL, cacheDir, faviconRootFilename);
		if (fileName != null)
			return FaviconUtils.createCachedFaviconAddress(contextRoot, fileName);
		else
			return null;
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	protected Path getCacheDirectoryPath() {
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

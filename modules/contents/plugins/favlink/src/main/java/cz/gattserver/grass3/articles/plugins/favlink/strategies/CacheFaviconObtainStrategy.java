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

	protected abstract void onCacheMiss(URL pageURL, Path targetFile);

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	public CacheFaviconObtainStrategy() {
		SpringContextHelper.inject(this);
	}

	@Override
	public String obtainFaviconURL(String pageAddress, String contextRoot) {
		URL pageURL = FaviconUtils.getPageURL(pageAddress);
		String faviconFilename = FaviconUtils.createFaviconFilename(pageURL);
		Path cachedFilePath = createCachedFilePath(faviconFilename);
		if (!Files.exists(cachedFilePath))
			onCacheMiss(pageURL, cachedFilePath);
		if (!Files.exists(cachedFilePath))
			return null;
		return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconFilename);
	}

	protected Path createCachedFilePath(String faviconFilename) {
		Path cacheDir = getCacheDirectoryPath();
		Path filePath = cacheDir.resolve(faviconFilename);
		return filePath;
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

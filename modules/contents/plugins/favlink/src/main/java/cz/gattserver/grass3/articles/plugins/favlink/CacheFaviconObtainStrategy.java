package cz.gattserver.grass3.articles.plugins.favlink;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.web.common.spring.SpringContextHelper;

/**
 * Snaží se získat favicon z cache již stažených ikon
 * 
 * @author Hynek
 *
 */
public abstract class CacheFaviconObtainStrategy implements FaviconObtainStrategy {

	protected abstract void onCacheMiss(URL pageURL, File targetFile);

	@Override
	public String obtainFaviconURL(String pageAddress, String contextRoot) {
		URL pageURL = getPageURL(pageAddress);
		String faviconFilename = createFaviconFilename(pageURL);
		File cachedFile = createCacheFile(faviconFilename);
		if (!cachedFile.exists())
			onCacheMiss(pageURL, cachedFile);
		if (!cachedFile.exists())
			return null;
		return createCachedFaviconAddress(contextRoot, faviconFilename);
	}

	/**
	 * Vytvoří adresu, na které bude dostupný favicon soubor z cache
	 * 
	 * @param contextRoot
	 *            kořenové URL, od kterého se budou vytváře interní linky
	 *            aplikace
	 * @param faviconFilename
	 *            název favicon souboru, ke kterému je adresa vytvářena
	 * @return URL adresa k favicon souboru z cache
	 */
	protected String createCachedFaviconAddress(String contextRoot, String faviconFilename) {
		return contextRoot + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/" + faviconFilename;
	}

	protected URL getPageURL(String pageAddress) {
		try {
			URL url = new URL(pageAddress);
			return url;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ParserException("Nezdařilo se vytěžit název domény ze zadané adresy", e);
		}
	}

	/**
	 * Vytvoří název ikony dle předaného URL odkazu, ke kterému favicon hledám
	 * 
	 * @param pageURL
	 *            odkaz, ke kterému hledám favicon
	 * @return název favicon souboru, například "google.com.png"
	 */
	protected String createFaviconFilename(URL pageURL) {
		String faviconFilename = pageURL.getHost() + ".png";
		return faviconFilename;
	}

	protected File createCacheFile(String faviconFilename) {
		File cacheDir = getCacheDirectoryPath();
		File file = new File(cacheDir, faviconFilename);
		return file;
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	protected File getCacheDirectoryPath() {
		ConfigurationService configurationService = (ConfigurationService) SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);

		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);

		// existuje cesta cache ?
		File cacheDir = new File(configuration.getOutputPath());
		if (cacheDir.exists()) {
			if (!cacheDir.isDirectory())
				throw new ParserException("Favicon cache path is not a directory");
		} else {
			if (!cacheDir.mkdirs())
				throw new ParserException("Favicon cache directory creation failed");
		}

		return cacheDir;
	}

}

package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import java.net.URL;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.plugins.favlink.FaviconUtils;

/**
 * @author gatt
 */
public class AddressFaviconObtainStrategy extends CacheFaviconObtainStrategy {

	private static final Logger logger = LoggerFactory.getLogger(AddressFaviconObtainStrategy.class);
	private static final String HTTP_PREFIX = "http://";
	private static final String FAVICON_ICO = "favicon.ico";
	private static final String FAVICON_PNG = "favicon.png";

	@Override
	protected String onCacheMiss(URL pageURL, Path cacheDir, String faviconRootFilename) {
		String address = HTTP_PREFIX + pageURL.getHost();

		// root + /favicon.ico
		logger.info("Trying favicon.ico");
		String icoFavicon = faviconRootFilename + ".ico";
		if (FaviconUtils.downloadFile(cacheDir.resolve(icoFavicon), address + "/" + FAVICON_ICO))
			return icoFavicon;

		// root + /favicon.png
		String pngFavicon = faviconRootFilename + ".png";
		logger.info("Trying favicon.png");
		if (FaviconUtils.downloadFile(cacheDir.resolve(pngFavicon), address + "/" + FAVICON_PNG))
			return pngFavicon;

		return null;
	}
}

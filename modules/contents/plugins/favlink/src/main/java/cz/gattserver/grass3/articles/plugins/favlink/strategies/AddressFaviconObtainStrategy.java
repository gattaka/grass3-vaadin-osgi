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
	protected void onCacheMiss(URL pageURL, Path targetFile) {
		String address = HTTP_PREFIX + pageURL.getHost();

		// root + /favicon.ico
		logger.info("Trying favicon.ico");
		if (FaviconUtils.downloadFile(targetFile, address + "/" + FAVICON_ICO))
			return;

		// root + /favicon.png
		logger.info("Trying favicon.png");
		if (FaviconUtils.downloadFile(targetFile, address + "/" + FAVICON_PNG))
			return;
	}
}

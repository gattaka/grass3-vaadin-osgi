package cz.gattserver.grass3.articles.plugins.favlink;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gatt
 */
public class HeaderFaviconObtainStrategy extends CacheFaviconObtainStrategy {

	private static final Logger logger = LoggerFactory.getLogger(HeaderFaviconObtainStrategy.class);
	private static final String HTTP_PREFIX_SHORT = "http:";
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";

	private String createFullFaviconAddress(String faviconAddress, String baseURI) {

		// je potřeba z Jsoup doc.baseUri(), protože to může být i vložená
		// stránka a tam se baseURI liší od počátečního
		// url.getHost() hlavní stránky

		logger.info("Favicon address found on page, address: " + faviconAddress);
		if (faviconAddress.startsWith(HTTP_PREFIX) || faviconAddress.startsWith(HTTPS_PREFIX)) {
			// absolutní cesta pro favicon
			logger.info("Trying download favicon from: " + faviconAddress);
			return faviconAddress;
		} else if (faviconAddress.startsWith("//")) {
			// absolutní cesta pro favicon, která míst 'http://' začíná jenom
			// '//'
			// tahle to má například stackoverflow
			String faviconFullAddress = HTTP_PREFIX_SHORT + faviconAddress;
			logger.info("Trying download favicon from: " + faviconFullAddress);
			return faviconFullAddress;
		} else {
			// relativní cesta pro favicon
			String faviconFullAddress = baseURI + "/" + faviconAddress;
			logger.info("Trying download favicon from: " + faviconFullAddress);
			return faviconFullAddress;
		}
	}

	private String findFaviconAddressOnPage(URL pageURL) {
		Document doc;
		try {

			// http://en.wikipedia.org/wiki/Favicon
			// need http protocol
			// bez agenta to často hodí 403 Forbidden, protože si myslí, že jsem
			// asi bot ... (což vlastně jsem)
			doc = Jsoup.connect(pageURL.toString()).userAgent("Mozilla").get();

			String ico;

			// link ICO (upřednostňuj)
			Element element = doc.head().select("link[href~=.*\\.ico]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, doc.baseUri());
			}

			// link PNG
			element = doc.head().select("link[href~=.*\\.png]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, doc.baseUri());
			}

			// meta + content
			element = doc.head().select("meta[itemprop=image]").first();
			if (element != null) {
				ico = element.attr("content");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, doc.baseUri());
			}

		} catch (IOException e) {
			logger.info("Nezdařilo se získat favicon z: {}", pageURL);
		}

		return null;
	}

	@Override
	protected void onCacheMiss(URL pageURL, File targetFile) {
		logger.info("Zkouším hledat v hlavičce");
		String faviconAddress = findFaviconAddressOnPage(pageURL);
		DownloadUtils.tryDownloadFavicon(targetFile, faviconAddress);
	}

}

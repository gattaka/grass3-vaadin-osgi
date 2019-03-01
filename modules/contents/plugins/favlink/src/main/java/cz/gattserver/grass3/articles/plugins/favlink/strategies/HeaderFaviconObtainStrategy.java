package cz.gattserver.grass3.articles.plugins.favlink.strategies;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.plugins.favlink.FaviconCache;
import cz.gattserver.grass3.articles.plugins.favlink.FaviconUtils;

/**
 * Strategie, která získává favicon adresu pomocí parsování HTML stránky a
 * hledání odkazů na favicon soubor.
 * 
 * @author gatt
 */
public class HeaderFaviconObtainStrategy implements FaviconObtainStrategy {

	private static final Logger logger = LoggerFactory.getLogger(HeaderFaviconObtainStrategy.class);
	private static final String HTTP_PREFIX_SHORT = "http:";
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";

	private FaviconCache cache;

	public HeaderFaviconObtainStrategy(FaviconCache cache) {
		this.cache = cache;
	}

	@Override
	public String obtainFaviconURL(String pageURL, String contextRoot) {
		logger.info("Zkouším hledat v hlavičce");

		URL url = FaviconUtils.getPageURL(pageURL);
		String faviconAddress = findFaviconAddressOnPage(url);
		if (faviconAddress == null)
			return null;

		String faviconFilename = cache.downloadAndSaveFavicon(url, faviconAddress);
		if (faviconFilename != null)
			return FaviconUtils.createCachedFaviconAddress(contextRoot, faviconFilename);

		return null;
	}

	private String createFullFaviconAddress(String faviconAddress, String baseURI) {

		// je potřeba z Jsoup doc.baseUri(), protože to může být i vložená
		// stránka a tam se baseURI liší od počátečního
		// url.getHost() hlavní stránky

		String tryMsg = "Zkouším stáhnout favicon z: {}";

		logger.info("Favicon adresa nalezena na: {}", faviconAddress);
		if (faviconAddress.startsWith(HTTP_PREFIX) || faviconAddress.startsWith(HTTPS_PREFIX)) {
			// absolutní cesta pro favicon
			logger.info(tryMsg, faviconAddress);
			return faviconAddress;
		} else if (faviconAddress.startsWith("//")) {
			// absolutní cesta pro favicon, která místo 'http://' začíná jenom
			// '//' tahle to má například stackoverflow
			String faviconFullAddress = HTTP_PREFIX_SHORT + faviconAddress;
			logger.info(tryMsg, faviconFullAddress);
			return faviconFullAddress;
		} else {
			// relativní cesta pro favicon
			String faviconFullAddress = baseURI + (faviconAddress.startsWith("/") ? "" : "/") + faviconAddress;
			logger.info(tryMsg, faviconFullAddress);
			return faviconFullAddress;
		}
	}

	private String findFaviconAddressOnPage(URL pageURL) {
		Document doc;
		String rootURL = pageURL.getProtocol() + "://" + pageURL.getHost();
		if (pageURL.getPort() > 0)
			rootURL += ":" + pageURL.getPort();

		try {
			// http://en.wikipedia.org/wiki/Favicon
			// need http protocol
			// bez agenta to často hodí 403 Forbidden, protože si myslí, že jsem
			// asi bot ... (což vlastně jsem)
			doc = Jsoup.connect(pageURL.toString()).userAgent("Mozilla").get();

			String ico;

			// link ICO (upřednostňuj)
			logger.info("Zkouším ICO hlavičku");
			Element element = doc.head().select("link[href~=.*\\.ico]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, rootURL);
			}

			// link PNG
			logger.info("Zkouším PNG hlavičku");
			element = doc.head().select("link[href~=.*\\.png]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, rootURL);
			}

			// meta + content
			logger.info("Zkouším META content");
			element = doc.head().select("meta[itemprop=image]").first();
			if (element != null) {
				ico = element.attr("content");
				if (StringUtils.isNotBlank(ico))
					return createFullFaviconAddress(ico, rootURL);
			}

			// link shortcut icon
			// logger.info("Zkouším LINK REL shortcut icon content");
			// element = doc.head().select("meta[itemprop=image]").first();
			// if (element != null) {
			// ico = element.attr("content");
			// if (StringUtils.isNotBlank(ico))
			// return createFullFaviconAddress(ico, rootURL);
			// }

		} catch (IOException e) {
			logger.error("Nezdařilo se získat stránku z pageURL: {}", pageURL);
		}

		logger.info("Nezdařilo se získat favicon z: {}", pageURL);
		return null;
	}

}

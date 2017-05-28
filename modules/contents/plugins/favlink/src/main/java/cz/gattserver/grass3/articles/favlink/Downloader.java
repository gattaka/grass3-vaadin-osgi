package cz.gattserver.grass3.articles.favlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gatt
 */
public class Downloader {

	private static final Logger logger = LoggerFactory.getLogger(Downloader.class);
	private static String HTTP_PREFIX_SHORT = "http:";
	private static String HTTP_PREFIX = "http://";
	private static String HTTPS_PREFIX = "https://";
	private static String FAVICON_ICO = "favicon.ico";
	private static String FAVICON_PNG = "favicon.png";

	private Downloader() {
	}

	private static InputStream getResponseReader(String address) {
		URL url = null;
		InputStream is = null;
		try {
			// musí se odstranit, protože například právě pro VAADIN je tento
			// lokální krok příčinou, proč se vrátí
			// DOCUMENT response s neplatnou session, namísto adresovaného
			// souboru favicony
			address = address.replace("/./", "/");
			url = new URL(address);
			URLConnection uc = url.openConnection();
			if (uc != null) {
				if (uc instanceof HttpURLConnection) {
					// HttpURLConnection
					HttpURLConnection hc = (HttpURLConnection) uc;
					hc.setInstanceFollowRedirects(true);

					// bez agenta to často hodí 403 Forbidden, protože si myslí,
					// že jsem asi bot ... (což vlastně jsem)
					hc.setRequestProperty("User-Agent", "Mozilla");
					logger.info("Favicon URL: " + uc.getURL());
					hc.setConnectTimeout(1000);
					hc.setReadTimeout(1000);
					hc.connect();

					// Zjisti, zda bude potřeba manuální redirect (URLConnection
					// to umí samo, dokud se nepřechází mezi
					// HTTP a HTTPS, pak to nechává na manuální obsluze)
					int responseCode = hc.getResponseCode();
					if (responseCode == 301 || responseCode == 302 || responseCode == 303) {
						String location = hc.getHeaderField("Location");
						hc = (HttpURLConnection) (new URL(location).openConnection());
						hc.setInstanceFollowRedirects(false);
						hc.setRequestProperty("User-Agent", "Mozilla");
						hc.connect();
					}

					logger.info("Favicon connected URL: " + hc.getURL());
					is = hc.getInputStream();
					logger.info("Favicon redirected URL: " + hc.getURL());
					if (is != null) {
						logger.info("Engine: InputStream obtained");
					} else {
						logger.info("Engine: InputStream is null !");
					}
					return is;
				}
			} else {
				logger.info("Engine: URL connection failed !");
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// hm...
				}
			}
		}
		return null;
	}

	private static String createFullFaviconAddress(String faviconAddress, String baseURI) {

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

	private static String findFaviconAddressOnPage(String address) {
		Document doc;
		try {

			// http://en.wikipedia.org/wiki/Favicon
			// need http protocol
			// bez agenta to často hodí 403 Forbidden, protože si myslí, že jsem
			// asi bot ... (což vlastně jsem)
			doc = Jsoup.connect(address).userAgent("Mozilla").get();

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
			e.printStackTrace();
		}

		return null;
	}

	private static int tryDownloadFavicon(File targetFile, String address) throws IOException {
		if (StringUtils.isBlank(address))
			return 0;
		InputStream stream = getResponseReader(address);
		int size = 0;
		if (stream != null) {
			OutputStream out = new FileOutputStream(targetFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = stream.read(buf)) > 0) {
				out.write(buf, 0, len);
				size += len;
			}
			out.close();
			stream.close();
			return size;
		}
		return 0;
	}

	public static void download(File targetFile, URL url) throws IOException {
		String address = HTTP_PREFIX + url.getHost();

		int downloadSize = 0;

		// root + /favicon.ico
		if (downloadSize == 0) {
			logger.info("Trying favicon.ico");
			downloadSize = tryDownloadFavicon(targetFile, address + "/" + FAVICON_ICO);
		}

		// root + /favicon.png
		if (downloadSize == 0) {
			logger.info("Trying favicon.png");
			downloadSize = tryDownloadFavicon(targetFile, address + "/" + FAVICON_PNG);
		}

		// page info
		if (downloadSize == 0) {
			logger.info("Trying page locations");
			String faviconAddress = findFaviconAddressOnPage(address);
			downloadSize = tryDownloadFavicon(targetFile, faviconAddress);
		}

		// zdařilo se?
		if (downloadSize == 0)
			targetFile.delete();

		logger.info("Done");
	}
}

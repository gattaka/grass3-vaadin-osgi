package cz.gattserver.grass3.articles.favlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
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
		try {
			// musí se odstranit, protože například právě pro VAADIN je tento lokální krok příčinou, proč se vrátí
			// DOCUMENT response s neplatnou session, namísto adresovaného souboru favicony
			address = address.replace("/./", "/");
			url = new URL(address);
			URLConnection uc = url.openConnection();
			if (uc != null) {
				logger.info("Favicon URL: " + uc.getURL());
				uc.connect();
				logger.info("Favicon connected URL: " + uc.getURL());
				InputStream is = uc.getInputStream();
				logger.info("Favicon redirected URL: " + uc.getURL());
				if (is != null) {
					logger.info("Engine: InputStream obtained");
				} else {
					logger.info("Engine: InputStream is null !");
				}
				return is;
			} else {
				logger.info("Engine: URL connection failed !");
			}
		} catch (MalformedURLException ex) {
			logger.error(ex.toString());
		} catch (IOException ex) {
			logger.error(ex.toString());
		}
		return null;
	}

	private static String findFaviconAddressOnPage(String address) {
		Document doc;
		try {

			// http://en.wikipedia.org/wiki/Favicon
			// need http protocol
			// bez agenta to často hodí 403 Forbidden, protože si myslí, že jsem asi bot ... (což vlastně jsem)
			doc = Jsoup.connect(address).userAgent("Mozilla").get();

			String ico;

			// link
			Element element = doc.head().select("link[href~=.*\\.(ico|png)]").first();
			if (element != null) {
				ico = element.attr("href");
				if (StringUtils.isNotBlank(ico))
					return ico;
			}

			// meta + content
			element = doc.head().select("meta[itemprop=image]").first();
			if (element != null) {
				ico = element.attr("content");
				if (StringUtils.isNotBlank(ico))
					return ico;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void download(File targetFile, URL url) throws IOException {
		String address = HTTP_PREFIX + url.getHost();
		String faviconAddress = findFaviconAddressOnPage(address);

		InputStream stream;

		if (StringUtils.isBlank(faviconAddress)) {
			logger.info("Favicon address NOT found on page, trying root locations");
			// root + /favicon.ico
			stream = getResponseReader(address + "/" + FAVICON_ICO);

			// root + /favicon.png
			if (stream == null) {
				stream = getResponseReader(address + "/" + FAVICON_PNG);
			}

		} else {
			logger.info("Favicon address found on page, address: " + faviconAddress);
			if (faviconAddress.startsWith(HTTP_PREFIX) || faviconAddress.startsWith(HTTPS_PREFIX)) {
				// absolutní cesta pro favicon
				logger.info("Trying download favicon from: " + faviconAddress);
				stream = getResponseReader(faviconAddress);
			} else if (faviconAddress.startsWith("//")) {
				// absolutní cesta pro favicon, která míst 'http://' začíná jenom '//'
				// tahle to má například stackoverflow
				String faviconFullAddress = HTTP_PREFIX_SHORT + faviconAddress;
				logger.info("Trying download favicon from: " + faviconFullAddress);
				stream = getResponseReader(faviconFullAddress);
			} else {
				// relativní cesta pro favicon
				String faviconFullAddress = address + "/" + faviconAddress;
				logger.info("Trying download favicon from: " + faviconFullAddress);
				stream = getResponseReader(faviconFullAddress);
			}
		}

		if (stream != null) {
			OutputStream out = new FileOutputStream(targetFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = stream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			stream.close();
		}
		logger.info("Done");
	}
}

package cz.gattserver.grass3.articles.plugins.favlink;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.plugins.favlink.config.FavlinkConfiguration;

/**
 * @author gatt
 */
public class FaviconUtils {

	private static final Logger logger = LoggerFactory.getLogger(FaviconUtils.class);

	private FaviconUtils() {
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

	/**
	 * Stáhne obsah dle adresy a uloží ho jako předaný {@link Path}
	 * 
	 * @param targetFile
	 *            cílový soubor, do kterého bude obsah uložen
	 * @param address
	 *            adresa obsahu, který bude stažen
	 * @return <code>true</code> pokud byl obsah úspěšně stažen a uložen do
	 *         souboru nebo <code>false</code>, pokud se ho nepovedlo stáhnout.
	 *         V případě, že chyba nastala až při ukládání je vyhozen
	 *         {@link ParserException}
	 */
	public static boolean downloadFile(Path targetFile, String address) {
		Validate.notNull(targetFile, "'targetFile' nesmí být null");
		Validate.notBlank(address, "'address' nesmí být null");
		logger.info("Ukládám favicon adresy {} jako {}", address, targetFile.toString());
		InputStream stream = getResponseReader(address);
		if (stream != null) {
			try {
				Files.copy(stream, targetFile);
				return true;
			} catch (IOException e) {
				throw new ParserException("Nezdařilo se uložit staženou favicon", e);
			}
		}
		return false;
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
	public static String createCachedFaviconAddress(String contextRoot, String faviconFilename) {
		Validate.notNull(contextRoot, "contextRoot nesmí být null");
		Validate.notBlank(faviconFilename, "faviconFilename nesmí být prázdný");
		return contextRoot + "/" + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/" + faviconFilename;
	}

	/**
	 * Získá {@link URL} z řetězce adresy nebo vyhodí {@link ParserException}.
	 * 
	 * @param pageAddress
	 *            webová adresa, která má být zpracována
	 * @return {@link URL} objekt dle adresy
	 */
	public static URL getPageURL(String pageAddress) {
		try {
			return new URL(pageAddress);
		} catch (MalformedURLException e) {
			throw new ParserException("Nezdařilo se vytěžit název domény ze zadané adresy", e);
		}
	}

	/**
	 * Vrátí jméno souboru favicony, dle adresy stránky, jejíž favicon se
	 * získává a adresy favicony. Bere tak v potaz příponu souboru.
	 * 
	 * @param pageURL
	 *            adresa stránky, jejíž favicon hledám (z ní získá základ názvu
	 *            souboru)-- například http://test.domain.com/neco/nekde
	 * @param faviconAddress
	 *            adresa souboru favicony, kterou jsem našel (z ní získá příponu
	 *            souboru favicony) -- například
	 *            http://test.domain.com/imgs/fav.ico
	 * @return název souboru favicony -- například test.domain.com.ico
	 */
	public static String getFaviconFilename(URL pageURL, String faviconAddress) {
		String faviconRootFilename = FaviconUtils.createFaviconRootFilename(pageURL);
		String extension = faviconAddress.substring(faviconAddress.lastIndexOf('.'), faviconAddress.length());
		// Odstraní případné ?param=value apod. za .pripona textem
		extension = extension.replaceAll("[^\\.A-Za-z0-9].+", "");
		return faviconRootFilename + extension;
	}

	/**
	 * Vytvoří název souboru ikony dle předaného URL odkazu, ke kterému favicon
	 * hledám.
	 * 
	 * @param pageURL
	 *            odkaz, ke kterému hledám favicon
	 * @return název favicon souboru bez přípony (tu ještě neznám)
	 */
	public static String createFaviconRootFilename(URL pageURL) {
		return pageURL.getHost();
	}

}

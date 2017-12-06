package cz.gattserver.grass3.articles.plugins.favlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gatt
 */
public class DownloadUtils {

	private static final Logger logger = LoggerFactory.getLogger(DownloadUtils.class);

	private DownloadUtils() {
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

	public static boolean tryDownloadFavicon(File targetFile, String address) {
		Validate.notNull(targetFile, "'targetFile' nesmí být null");
		Validate.notBlank(address, "'address' nesmí být null");
		InputStream stream = getResponseReader(address);
		try {
			if (stream != null) {
				OutputStream out = new FileOutputStream(targetFile);
				byte buf[] = new byte[1024];
				int len;
				while ((len = stream.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				stream.close();
				return true;
			}
		} catch (IOException e) {
			logger.error("Nezdařilo se uložit staženou favicon", e);
		}
		return false;
	}

}

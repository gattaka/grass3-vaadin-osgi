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

import com.vaadin.sass.internal.util.StringUtil;

/**
 * 
 * @author gatt
 */
public class Downloader {

	private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

	private String address;

	public Downloader(String address) {
		this.address = address;
		logger.info("Downloader: address " + address);
	}

	private InputStream getResponseReader(String address) {
		URL url = null;
		try {
			url = new URL(address);
			logger.info("Engine: URL set");
			URLConnection uc = url.openConnection();
			if (uc != null) {
				logger.info("Engine: URL connection made");
				InputStream is = uc.getInputStream();
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

	private String findFaviconFileAddress() {
		Document doc;
		try {

			// http://en.wikipedia.org/wiki/Favicon

			// need http protocol
			String httpPrefix = "http://";
			String addressToGET = address;
			if (address.startsWith(httpPrefix) == false) {
				addressToGET = httpPrefix + address;
			}

			// bez agenta to často hodí 403 Forbidden, protože si myslí, že jsem asi bot ... (což vlastně jsem)
			doc = Jsoup.connect(addressToGET).userAgent("Mozilla").get();

			String ico;

			// link
			Element element = doc.head().select("link[href~=.*\\.(ico|png)]").first();
			ico = element.attr("href");
			if (StringUtils.isNotBlank(ico))
				return ico;

			// meta + content
			element = doc.head().select("meta[itemprop=image]").first();
			ico = element.attr("content");
			if (StringUtils.isNotBlank(ico))
				return ico;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void download(File targetFile) throws IOException {
		InputStream stream = getResponseReader(findFaviconFileAddress());
		OutputStream out = new FileOutputStream(targetFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = stream.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
		stream.close();
		logger.info("Done");
	}

}

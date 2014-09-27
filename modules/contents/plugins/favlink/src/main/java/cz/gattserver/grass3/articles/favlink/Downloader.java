package cz.gattserver.grass3.articles.favlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gatt
 */
public class Downloader {

	private static final Logger logger = LoggerFactory
			.getLogger(Downloader.class);

	private static String engineAddress = "http://g.etfv.co/";
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

	public void download(File targetFile) throws IOException {

		InputStream stream = getResponseReader(engineAddress + address);
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

package org.myftp.gattserver.grass3.articles.favlink.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;
import org.myftp.gattserver.grass3.articles.favlink.Downloader;
import org.myftp.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkTree extends AbstractElementTree {

	private static final Logger logger = LoggerFactory.getLogger(LinkTree.class);

	private String link;
	private final static String defaultFavicon = "/img/tags/label_16.png"; // default
	private String imgURL = null;
	private String contextRoot;

	public LinkTree(String link, String contextRoot) {
		this.link = link.trim();
		this.contextRoot = contextRoot;
		setLink();
	}

	private void setLink() {
		String favicon = faviconImg();
		// pokud favicon nebylo možné získat, pak vlož výchozí
		if (favicon != null) {
			imgURL = favicon;
		} else {
			imgURL = defaultFavicon;
		}
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	private String getCachePath() {

		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");

		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration.getOutputPath();
	}

	private String faviconImg() {

		// 1) Cache
		String path = getCachePath();

		// existuje cesta cache ?
		File cacheFile = new File(path);
		if (cacheFile.exists()) {
			if (cacheFile.isFile()) {
				logger.error("Cache path exists as file ! Aborting ...");
				throw new ParserException();
			}
		} else {
			if (cacheFile.mkdirs() == false) {
				logger.error("Cache path creation failed ! Aborting ...");
				throw new ParserException();
			}
		}

		// 2) Zjisti identifikační část URL, podle které se bude hledat
		// jméno
		try {
			URL url = new URL(link);
			String domain = url.getHost() + ".png";

			File file = new File(path, domain);

			// pokud neexistuje, pak jej stáhni
			if (!file.exists()) {
				Downloader d = new Downloader(link);
				d.download(file);
			}

			// 3) vrat URL na cache
			return contextRoot + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/" + domain;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ParserException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParserException();
		}

	}

	@Override
	public void generateElement(IContext ctx) {
		ctx.print("<a href=\"" + link + "\" alt=\"" + link + "\" >");
		ctx.print("<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"" + imgURL
				+ "\" alt=\"Favicon of " + link + "\" title=\"Favicon of " + link + "\" />");
		ctx.print(link);
		ctx.print("</a>");
	}
}

package org.myftp.gattserver.grass3.articles.favlink.service.impl;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nedědí ze třídy {@link HTTPServiceTracker}, protože LaTeX plugin má možnost
 * za běhu přenastavit úložiště PNG výstupů z překladu rovnic a musí tak
 * "force-nout" tracker aby přemapoval alias na nový výstupní adresář. Navíc
 * LaTeX potřebuje registrovat takovouhle resource jenom jednu, takže se vyplatí
 * si napsat vlastní {@link ServiceTracker}, než ohýbat předkonfigurovaný
 * {@link HTTPServiceTracker}
 * 
 * @author gatt
 * 
 */
public class FavlinkPluginImagesHttpServer {

	private HttpService httpService;
	private String alias = null;
	private Logger logger = LoggerFactory
			.getLogger(FavlinkPluginImagesHttpServer.class);

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	private String getOutputPath() {
		try {
			return new ConfigurationUtils<FavlinkConfiguration>(
					new FavlinkConfiguration(),
					FavlinkConfiguration.CONFIG_PATH)
					.loadExistingOrCreateNewConfiguration().getOutputPath();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new ParserException();
		}
	}

	/**
	 * Zaregistruje nebo přeregistruje alias pro adresář s výstupem překladu
	 * Latexových rovnic
	 * 
	 * @param alias
	 * @param path
	 */
	public void registerNewLatexOutputDir(String path) {

		if (httpService == null)
			throw new IllegalStateException();

		if (alias != null && !alias.isEmpty())
			this.httpService.unregister(alias);
		alias = FavlinkConfiguration.IMAGE_PATH_ALIAS;

		try {
			httpService.registerResources(alias, path,
					new FaviconLinkResourceContext());
			logger.info("HttpService registration: " + alias);
		} catch (NamespaceException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Unable to mount [" + path
					+ "] with alias '" + alias + "'.");
		}

	}

	public void init() {

		// pokud je alias prázdný, tak zaregistruj ten default
		if (alias == null || alias.isEmpty()) {
			alias = FavlinkConfiguration.IMAGE_PATH_ALIAS;
			try {
				httpService.registerResources(alias, getOutputPath(),
						new FaviconLinkResourceContext());
			} catch (NamespaceException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Unable to mount ["
						+ FavlinkConfiguration.CONFIG_PATH + "] with alias '"
						+ alias + "'.");
			}
		}

	}

	public void destroy() {
		if (alias != null && !alias.isEmpty())
			this.httpService.unregister(alias);
	}

	public HttpService getHttpService() {
		return httpService;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

}

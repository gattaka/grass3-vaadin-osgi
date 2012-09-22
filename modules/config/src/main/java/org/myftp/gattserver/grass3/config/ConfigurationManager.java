package org.myftp.gattserver.grass3.config;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Hlavní konfigurační třída, poskytovaná jako služba
 * 
 * @author gatt
 * 
 */
public class ConfigurationManager {

	/**
	 * Aktuálně používaná konfigurace
	 */
	private DispatcherConfiguration dispatcherConfiguration;
	private final String dispatcherConfigurationFilename = "grass_dispatcher_config.xml";

	/**
	 * Soubor s cestou k konfiguracím
	 */
	private final File dispatcherConfigurationFile = new File(
			dispatcherConfigurationFilename);

	/**
	 * Instance {@link ConfigurationManager}
	 */
	private static ConfigurationManager configurationManager;

	/**
	 * Singleton
	 */
	public static ConfigurationManager getInstance() {
		if (configurationManager == null)
			configurationManager = new ConfigurationManager();
		return configurationManager;
	}

	private void log(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * 
	 * @return true pokud se zdařilo, false pokud ne
	 */
	private boolean tryLoad() {

		// pokud existuje soubor
		if (dispatcherConfigurationFile.exists()) {
			try {
				dispatcherConfiguration = (DispatcherConfiguration) loadConfiguration(
						dispatcherConfigurationFile,
						DispatcherConfiguration.class);
				if ((dispatcherConfiguration.getGrassVersion()
						.equals(AppInfo.GRASS_VERSION))
						&& (dispatcherConfiguration.getGrassName()
								.equals(AppInfo.GRASS_NAME))) {

				} else {
					// neodpovídají verze
					return false;
				}
			} catch (JAXBException e) {
				e.printStackTrace();
				// došlo k chybě při parsování XML
				return false;
			}
		} else {
			// soubor neexistuje
			return false;
		}

		try {
			// nastav aby cesta byla rovnou v canonickém formátu (aby se dala
			// porovnávat její délka s dalšími canonckými) viz. createValidFile
			dispatcherConfiguration.setConfigurationPath(new File(
					dispatcherConfiguration.getConfigurationPath())
					.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			// chyba cesty
			return false;
		}
		return true;
	}

	/**
	 * Základní konstruktor, nejprve musí lokalizovat umístění konfigurací, k
	 * tomu slouží soubor grass_dispatcher_config.xml, jehož umístění je pevně
	 * dané. Pokud ho nenajde, vytvoří implicitně nové úložiště konfigurací.
	 */
	private ConfigurationManager() throws ConfigurationFileError {

		if (tryLoad() == false) {
			// nezdařilo se najít základní soubor konfigurace nebo je s ním
			// nějaký problém při čtení - pokud soubor existuje, pak s ním
			// raději nebudu nic dělat a ohlásím pád, jinak jen vyhodím varování
			// a provedu inicializaci
			if (dispatcherConfigurationFile.exists()) {
				log("ERR: corrupted or unmatching system version configuration dispatcher file \""
						+ dispatcherConfigurationFilename + "\" found");
				log("ERR: unable to continue without risk - provide valid XML or configuration dispatcher file of proper system version "
						+ AppInfo.GRASS_VERSION);
				throw new ConfigurationFileError();
			} else {
				log("WARN: missing configuration dispatcher file \""
						+ dispatcherConfigurationFilename + "\"");
				log("WARN: system will continue in intialization by creating a new one with new config dir");
				log("WARN: but this might by a sign of some problem in your installation and therefor it should not be ignored");
				try {
					storeConfiguration(
							new File(dispatcherConfigurationFilename)
									.getCanonicalFile(),
							dispatcherConfiguration = new DispatcherConfiguration());
				} catch (JAXBException e) {
					e.printStackTrace();
					log("ERR: unable to create a new configuration dispatcher file \""
							+ dispatcherConfigurationFilename + "\"");
					throw new ConfigurationFileError();
				} catch (IOException e) {
					e.printStackTrace();
					throw new ConfigurationFileError();
				}
			}
		} else {
			log("INFO: configuration dispatcher file \""
					+ dispatcherConfigurationFilename + "\" found");
		}

	}

	/**
	 * Převede jméno souboru konfigurace na plný název a z něj vytvoří
	 * {@link File}, zkontroluje ale, zda nedochází k "podtečení" cesty, pomocí
	 * ".." znaků - pokud ano, vyhodí exception
	 * 
	 * @param filename
	 *            jméno souboru (relativní cesta od adresáře konfigurací)
	 * @return {@link File} na soubor
	 */
	private File createValidFile(String filename) throws ConfigurationFileError {
		try {
			File file = new File(dispatcherConfiguration.getConfigurationPath()
					+ "/" + filename).getCanonicalFile();
			if (file.getPath().length() <= dispatcherConfiguration
					.getConfigurationPath().length())
				throw new ConfigurationFileError();
			else {
				return file;
			}
		} catch (IOException e) {
			throw new ConfigurationFileError();
		}

	}

	/**
	 * Metoda na uložení nějaké konfigurace do nějakého souboru
	 * 
	 * @param fileName
	 *            jméno souboru - jde o relativní cestu od adresáře konfigurací,
	 *            jehož cesta je určena v config dispatcheru
	 * @param configuration
	 *            instance objektu s konfiguračními údaji
	 * @param configurationClass
	 *            třída, jejíž instance je posílána k uložení
	 * @throws JAXBException
	 */
	public void storeConfiguration(String fileName, Object configuration)
			throws JAXBException, ConfigurationFileError {
		storeConfiguration(createValidFile(fileName), configuration);
	}

	private void storeConfiguration(File file, Object configuration)
			throws JAXBException, ConfigurationFileError {
		JAXBContext context = JAXBContext.newInstance(configuration.getClass());

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		// ještě zkontroluj, zda adresář kam chci dát soubor existuje, pokud ne,
		// vytvoř ho a pokračuj v ukládání
		if (file.getParentFile().exists() == false) {
			try {
				if (file.getParentFile().mkdirs() == false)
					throw new ConfigurationFileError();
			} catch (SecurityException e) {
				throw new ConfigurationFileError();
			}
		}

		marshaller.marshal(configuration, file);
	}

	/**
	 * Metoda k nahrání konfigurace
	 * 
	 * @param fileName
	 *            jméno souboru - jde o relativní cestu od adresáře konfigurací,
	 *            jehož cesta je určena v config dispatcheru
	 * @param configurationClass
	 *            třída, jejíž instance je načítána
	 * @return instance objektu s konfiguračními údaji nebo null, pokud soubor
	 *         neexistuje
	 * @throws JAXBException
	 */
	public Object loadConfiguration(String fileName, Class<?> configurationClass)
			throws JAXBException {
		File file = createValidFile(fileName);
		if (file.exists()) {
			return loadConfiguration(file, configurationClass);
		} else {
			return null;
		}
	}

	private Object loadConfiguration(File file, Class<?> configurationClass)
			throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(configurationClass);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		return unmarshaller.unmarshal(file);
	}

}

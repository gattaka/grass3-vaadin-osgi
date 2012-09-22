package org.myftp.gattserver.grass3.fm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.fm.config.Configuration;

public class FMExplorer {

	/**
	 * Absolutní cesta od systémového kořene ke kořeni FM
	 */
	private String absoluteRootDirPath;

	/**
	 * Absolutní cesta od systémového kořene ke kořeni FM, jako {@link File}
	 * objekt
	 */
	private File absoluteRootDirFile;

	/**
	 * Absolutní cesta od systémového kořene k souboru FM
	 */
	private String absoluteRequestedPath;

	/**
	 * Absolutní cesta od systémového kořene k souboru FM, jako {@link File}
	 * objekt
	 */
	private File absoluteRequestedFile;

	/**
	 * Absolutní cesta od systémového kořene k tmp adresáři, jako {@link File}
	 * objekt
	 */
	private File absoluteTmpDirFile;

	/**
	 * Absolutní cesta od systémového kořene k tmp adresáři
	 */
	private String absoluteTmpDirPath;

	/**
	 * Konfigurace FM
	 */
	private Configuration configuration;

	/**
	 * Byla cesta souboru odvozena od souboru ?
	 */
	private boolean pathDerivedFromFile = false;

	/**
	 * Bylo potřeba se vrátit do kořene, protože požadovaný soubor nevyhovoval ?
	 */
	private boolean forcedToRoot = false;

	/**
	 * Ověří, že požadovaný adresář nepodtéká adresář kořenový
	 * 
	 * @param requestedAbsolutePath
	 *            testovaná absolutní cesta k souboru
	 * @param mustExist
	 *            je vyžadováno aby existoval ?
	 * @return absolutní cesta k souboru nebo null, pokud nevyhovoval
	 */
	public File validateAbsolutePath(String requestedAbsolutePath,
			boolean mustExist) {
		String path;
		File file;
		try {
			file = new File(requestedAbsolutePath).getCanonicalFile();
			path = file.getAbsolutePath();
		} catch (IOException e) {
			return null;
		}
		if (absoluteRootDirPath.length() > path.length())
			return null;
		if (mustExist == false || file.exists())
			return file;
		else
			return null;
	}

	/**
	 * Ověří existenci a validnost kořenového adresáře
	 * 
	 * @throws IOException
	 */
	private void loadRootDirFromConfiguration(Configuration configuration)
			throws IOException {

		String rootDir = configuration.getRootDir();

		// pokud kořenový adresář neexistuje vytvoř jej
		File rootFile = new File(rootDir);
		if (!rootFile.exists())
			if (rootFile.mkdirs())
				throw new IOException();

		absoluteRootDirFile = rootFile.getCanonicalFile();
		absoluteRootDirPath = absoluteRootDirFile.getPath();

	}

	/**
	 * Ověří existenci a validnost tmp adresáře pro upload souborů
	 * 
	 * @throws IOException
	 */
	private void loadUploadDirFromConfiguration(Configuration configuration)
			throws IOException {

		String tmpDir = configuration.getTmpDir();

		// pokud kořenový adresář neexistuje vytvoř jej
		File tmpFile = new File(tmpDir);
		if (!tmpFile.exists())
			if (tmpFile.mkdirs())
				throw new IOException();

		absoluteTmpDirFile = tmpFile.getCanonicalFile();
		absoluteTmpDirPath = absoluteTmpDirFile.getPath();

	}

	/**
	 * Získá aktuální konfiguraci ze souboru konfigurace
	 * 
	 * @return soubor konfigurace FM
	 * @throws JAXBException
	 */
	private Configuration loadConfiguration() throws JAXBException {
		return new ConfigurationUtils<Configuration>(new Configuration(),
				Configuration.FM_MODULE_CONFIG_PATH)
				.loadExistingOrCreateNewConfiguration();
	}

	/**
	 * Vytvoř nový adresář v aktuálním adresáři
	 * 
	 * @param name
	 *            jméno adresáře
	 * @return true pokud se vytvoření zdařilo, jinak false
	 */
	public boolean createNewDir(String name) {
		File newFile = new File(absoluteRequestedFile, name);
		return newFile.mkdir();
	}

	/**
	 * Spočítá do hloubky velikost adresáře - pokud je mu předán soubor, který
	 * není adresář vrátí jeho velikost.
	 * 
	 * TODO .. přetečení long hodnoty ?
	 * 
	 * @param file
	 *            adresář k spočítání
	 * @param skippedFiles
	 *            list do kterého budou nastaveny ty soubory, u kterých se
	 *            nezdařilo čtení velikosti a proto nemohly být započítány
	 * @return velikost adresáře včetně podadresářů
	 */
	public long getDeepDirSize(File file, List<File> skippedFiles) {

		if (file.isFile())
			return file.length();

		long size = 0L;
		File[] subFiles = file.listFiles();
		if (subFiles == null) {
			skippedFiles.add(file);
			return file.length();
		}
		for (File subfile : subFiles) {
			size += getDeepDirSize(subfile, skippedFiles);
		}

		return size;

	}

	/**
	 * Vytvoří popis velikosti souboru v lidsky čitelných hodnotách (kB, MB ...)
	 * 
	 * @param bytes
	 *            velikost jež se zpracovává
	 * @param si
	 *            mají být velikosti počítány jako binární ? (si = true) kB ~
	 *            1000, kiB ~ 1024 (si = false)
	 * @return řetězec s popisem velikosti souboru
	 */
	// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Uloží nahraný soubor
	 * 
	 * @param tmpFile
	 *            dočasný soubor
	 * @param filename
	 *            jeho název
	 * @return true pokud se podařilo, jinak false
	 */
	public boolean saveFile(File tmpFile, String filename) {

		if (tmpFile.exists() == false)
			return false;
		File destFile = new File(absoluteRequestedFile, filename);

		try {
			return tmpFile.renameTo(destFile);
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Smaže soubor
	 * 
	 * @param file
	 *            soubor
	 * @return true pokud se zdařilo, jinak false
	 */
	public boolean deleteFile(File file) {

		boolean clean = true;

		// lze smazat přímo ?
		if (file.isFile() || file.list() == null || file.list().length == 0) {
			// budeš smazán přímo (později)
		} else {
			for (File subFile : file.listFiles()) {
				clean = clean && deleteFile(subFile);
			}
		}

		// společné pro promazaný strom adresářů a pro jednoduchý soubor
		try {
			return clean && file.delete();
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Přejmenuje soubor
	 * 
	 * @param file
	 *            soubor
	 * @param newName
	 *            nové jméno
	 * @return true pokud ok, jinak false
	 */
	public boolean renameFile(File file, String newName) {
		return file.renameTo(new File(file.getParent(), newName));
	}

	/**
	 * Zvaliduje název nového adresáře
	 * 
	 * @param name
	 *            jméno
	 * @return true pokud je validní jinak false
	 */
	public boolean validateNewDirName(String name) {
		String invalidChars = configuration.getInvalidDirCharacters();
		for (int i = 0; i < invalidChars.length(); i++)
			for (int j = 0; j < name.length(); j++)
				if (name.charAt(j) == invalidChars.charAt(i))
					return false;
		return true;
	}

	public String getInvalidNewDirCharacters() {
		return configuration.getInvalidDirCharacters();
	}

	/**
	 * Vezme cestu k souboru a odstřihne od ní cestu k rootDir-u
	 * 
	 * @param file
	 *            soubor na zpracování
	 * @return koncová cesta k souboru od rootDir
	 * @throws IOException
	 */
	public String filePathFromRoot(File file) throws IOException {
		int rootPathLength = absoluteRootDirPath.length();
		String path;
		path = file.getCanonicalPath().substring(rootPathLength);
		return path.isEmpty() ? "/" : path;
	}

	/**
	 * {@link FMExplorer} začne v adresáři, který je podle konfigurace jako jeho
	 * root (omezení).
	 * 
	 * @param relativePath
	 *            cesta, ve kterém má {@link FMExplorer} začít - pokud narazí na
	 *            zabezpečovací chybu (podtečení root adreáře) nebo jiný
	 *            problém, vyhodí {@link IOException}
	 * @throws IOException
	 */
	public FMExplorer(String relativePath) throws IOException {

		processConfiguration();

		/**
		 * Zpracuj podadresář
		 */
		if ((absoluteRequestedFile = validateAbsolutePath(absoluteRootDirPath
				+ "/" + relativePath, true)) == null) {
			// chyba ? nastav root jako ten adresář
			absoluteRequestedFile = absoluteRootDirFile;
			forcedToRoot = true;
		}

		/**
		 * Pokud se jednalo přímo o soubor, nikoliv o adresář, zarovnej a nastav
		 * flag, že explorer byl odvozen od souboru, ne adresáře
		 */
		if (absoluteRequestedFile.isFile()) {
			pathDerivedFromFile = true;
			absoluteRequestedFile = absoluteRequestedFile.getParentFile();
		}

		absoluteRequestedPath = absoluteRequestedFile.getPath();
	}

	private void processConfiguration() throws IOException {

		/**
		 * Začni nahráním konfigurace
		 */
		try {
			configuration = loadConfiguration();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new IOException();
		}
		loadRootDirFromConfiguration(configuration);
		loadUploadDirFromConfiguration(configuration);

	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getAbsoluteRootDirPath() {
		return absoluteRootDirPath;
	}

	public File getAbsoluteRootDirFile() {
		return absoluteRootDirFile;
	}

	public String getAbsoluteRequestedPath() {
		return absoluteRequestedPath;
	}

	public File getAbsoluteRequestedFile() {
		return absoluteRequestedFile;
	}

	public boolean isPathDerivedFromFile() {
		return pathDerivedFromFile;
	}

	public String getTmpPath() {
		return absoluteTmpDirPath;
	}

	public boolean isForcedToRoot() {
		return forcedToRoot;
	}

}

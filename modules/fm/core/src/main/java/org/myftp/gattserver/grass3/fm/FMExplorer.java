package org.myftp.gattserver.grass3.fm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.fm.config.FMConfiguration;

public class FMExplorer {

	// Suffixy:
	// *URL - cesta k souboru/adresáři; nezávislá na platformě
	// *Path - cesta k souboru/adresáři; závislá na platformě (viz. separator)
	// *File - objekt java.io.File obsahující cestu k souboru/adresáři

	/**
	 * Absolutní cesta od systémového kořene ke kořeni FM, jako {@link File}
	 * objekt
	 */
	private File rootFile;

	/**
	 * Absolutní cesta od systémového kořene k souboru FM, jako {@link File}
	 * objekt
	 */
	private File requestedFile;

	/**
	 * Absolutní cesta od systémového kořene k tmp adresáři, jako {@link File}
	 * objekt
	 */
	private File tmpDirFile;

	/**
	 * Konfigurace FM
	 */
	private FMConfiguration configuration;

	/**
	 * Byla cesta souboru odvozena od souboru ?
	 */
	private boolean pathDerivedFromFile = false;

	/**
	 * V jakém stavu je zpracovávaný soubor ?
	 */
	private FileProcessState state = FileProcessState.SUCCESS;

	/**
	 * Stav zpracování souboru
	 */
	public static enum FileProcessState {
		SUCCESS, MISSING, NOT_VALID, ALREADY_EXISTS, SYSTEM_ERROR
	}

	/**
	 * {@link FMExplorer} začne v adresáři, který je podle konfigurace jako jeho
	 * root (omezení).
	 * 
	 * @param relativePath
	 *            cesta, ve kterém má {@link FMExplorer} začít - pokud narazí na
	 *            zabezpečovací chybu (podtečení root adreáře) nebo jiný
	 *            problém, vyhodí pokusí se použít kořenový adresář a důvod jeho
	 *            použití uloží do {@code state} proměnné
	 * @throws IOException
	 *             tuto chybu vyhazuje pouze pokud se nezdařilo pracovat ani s
	 *             kořenovým adresářem, jinak je přednostně tato chyba odchycena
	 *             a je použit právě kořenový adresář namísto předaného souboru
	 */
	public FMExplorer(String relativePath) throws IOException {

		processConfiguration();

		if (relativePath == null)
			relativePath = "";

		/**
		 * Vytvoř File z předávané relativní cesty
		 */
		requestedFile = new File(rootFile, relativePath);

		/**
		 * Otestuj, zda File existuje, pokud ne, přesměruj se na kořenový
		 * adresář a zapiš, že bylo nutné použít kořenový adresář kvůli
		 * neexistenci předávaného souboru
		 */
		if (requestedFile.exists() == false) {
			requestedFile = rootFile;
			state = FileProcessState.MISSING;
		}

		/**
		 * Převede soubor na canonický tvar, ve kterém jsou eliminovány veškeré
		 * části cesty, kvůli kterým by se nedaly soubory rychle porovnávat
		 * ("..",".","~" atd.)
		 */
		try {
			requestedFile = requestedFile.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
			requestedFile = rootFile;
			state = FileProcessState.SYSTEM_ERROR;
		}

		/**
		 * Zkontroluj validnost souboru, pokud není, přesměruj se na kořenový
		 * adresář a zapiš, že bylo nutné použít kořenový adresář kvůli
		 * nevalidnosti předávaného souboru
		 */
		if (isValid(requestedFile) == false) {
			requestedFile = rootFile;
			state = FileProcessState.NOT_VALID;
		}

	}

	private void processConfiguration() throws IOException {

		/**
		 * Začni nahráním konfigurace
		 */
		configuration = loadConfiguration();
		loadRootDirFromConfiguration(configuration);
		loadUploadDirFromConfiguration(configuration);

	}

	private void loadRootDirFromConfiguration(FMConfiguration configuration)
			throws IOException {

		String rootDir = configuration.getRootDir();

		// pokud kořenový adresář neexistuje vytvoř jej
		File rootFile = new File(rootDir);
		if (!rootFile.exists())
			if (rootFile.mkdirs())
				throw new IOException();

		this.rootFile = rootFile.getCanonicalFile();
	}

	private void loadUploadDirFromConfiguration(FMConfiguration configuration)
			throws IOException {

		String tmpDir = configuration.getTmpDir();

		// pokud kořenový adresář neexistuje vytvoř jej
		File tmpFile = new File(tmpDir);
		if (!tmpFile.exists())
			if (tmpFile.mkdirs())
				throw new IOException("Unable to create tmp directory for uploads");

		tmpDirFile = tmpFile.getCanonicalFile();
	}

	/**
	 * Získá aktuální konfiguraci ze souboru konfigurace
	 * 
	 * @return soubor konfigurace FM
	 * @throws JAXBException
	 */
	private FMConfiguration loadConfiguration() {
		IConfigurationService configurationService = (IConfigurationService) SpringContextHelper
				.getBean("configurationService");
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	/**
	 * Ověří, že požadovaný adresář nepodtéká adresář kořenový
	 * 
	 * @throws IOException
	 *             pokud testovaný soubor neexistuje
	 */
	public boolean isValid(File adeptFile) throws IOException {
		String rootDirCanonicalPath = rootFile.getCanonicalPath();
		String adeptFileCanonicalPath = adeptFile.getCanonicalPath();

		// adeptFile canonical cesta musí obsahovat rootFile canonical cestu
		return adeptFileCanonicalPath.startsWith(rootDirCanonicalPath);
	}

	/**
	 * Vytvoř nový adresář v aktuálním adresáři
	 * 
	 * @param name
	 *            jméno adresáře
	 */
	public FileProcessState createNewDir(String name) {
		File newFile = new File(requestedFile, name);
		try {
			newFile = newFile.getCanonicalFile();
			if (isValid(newFile) == false) {
				return FileProcessState.NOT_VALID;
			}
			if (newFile.exists()) {
				return FileProcessState.ALREADY_EXISTS;
			}
			if (newFile.mkdir() == false) {
				return FileProcessState.SYSTEM_ERROR;
			} else {
				return FileProcessState.SUCCESS;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Spočítá do hloubky velikost adresáře - pokud je mu předán soubor, který
	 * není adresář vrátí jeho velikost. MAX_VALUE Long je (2^63)-1, což je víc
	 * než 10^18, to je hodnota akorát velká pro pokrytí záznam Exbibajt (2^60)
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
	 * Uloží nahraný soubor
	 * 
	 * @param tmpFile
	 *            dočasný soubor
	 * @param filename
	 *            jeho název
	 * @return true pokud se podařilo, jinak false
	 */
	public FileProcessState saveFile(File tmpFile, String filename) {

		if (tmpFile.exists() == false)
			return FileProcessState.MISSING;

		File destFile = new File(requestedFile, filename);
		try {
			destFile = destFile.getCanonicalFile();
			if (destFile.exists()) {
				return FileProcessState.ALREADY_EXISTS;
			}
			if (isValid(destFile) == false) {
				return FileProcessState.NOT_VALID;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return FileProcessState.SYSTEM_ERROR;
		}

		try {
			if (tmpFile.renameTo(destFile) == false) {
				return FileProcessState.SYSTEM_ERROR;
			} else {
				return FileProcessState.SUCCESS;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			return FileProcessState.SYSTEM_ERROR;
		}

	}

	/**
	 * Smaže soubor
	 * 
	 * @param file
	 *            soubor
	 * @return true pokud se zdařilo, jinak false
	 */
	public FileProcessState deleteFile(File file) {

		try {
			file = file.getCanonicalFile();
			if (isValid(file) == false) {
				return FileProcessState.NOT_VALID;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return FileProcessState.SYSTEM_ERROR;
		}

		FileProcessState overallResult = FileProcessState.SUCCESS;

		// lze smazat přímo ?
		if (file.isFile() || file.list() == null || file.list().length == 0) {
			// budeš smazán přímo (později)
		} else {
			FileProcessState partialResult;
			for (File subFile : file.listFiles()) {
				partialResult = deleteFile(subFile);
				// pokud mazání skončilo chybou, zaznamenej ji, jinak ponech
				// poslední stav
				if (partialResult.equals(FileProcessState.SUCCESS) == false) {
					overallResult = partialResult;
				}
			}
		}

		// společné pro promazaný strom adresářů a pro jednoduchý soubor
		try {
			if (file.delete() == false) {
				return FileProcessState.SYSTEM_ERROR;
			} else {
				// vrať konjunkci výsledků mazání podadresářů - SUCCESS bude
				// tedy vrácen pouze tehdy, pokud byly všechna rekurzivní mazání
				// také hodnocena stavem SUCCESS
				return overallResult;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Přejmenuje soubor
	 * 
	 * @param file
	 *            soubor
	 * @param newName
	 *            nové jméno
	 */
	public FileProcessState renameFile(File file, String newName) {
		File renamedFile = new File(file.getParent(), newName);
		try {
			renamedFile = renamedFile.getCanonicalFile();
			if (isValid(renamedFile) == false) {
				return FileProcessState.NOT_VALID;
			}
			if (renamedFile.exists()) {
				return FileProcessState.ALREADY_EXISTS;
			}
			if (file.renameTo(renamedFile) == false) {
				return FileProcessState.SYSTEM_ERROR;
			} else {
				return FileProcessState.SUCCESS;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Vezme cestu k souboru a odstřihne od ní cestu k rootDir-u, předpokládá
	 * se, že file je již v canonickém tvaru a existuje
	 * 
	 * @param file
	 *            soubor na zpracování
	 * @return koncová cesta k souboru od rootDir
	 * @throws IOException
	 */
	public String fileURLFromRoot(File file) throws IOException {
		String rootURL = rootFile.toURI().toString();
		String fileURL = file.toURI().toString();
		return fileURL.substring(rootURL.length());
	}

	public FMConfiguration getConfiguration() {
		return configuration;
	}

	public boolean isPathDerivedFromFile() {
		return pathDerivedFromFile;
	}

	public FileProcessState getState() {
		return state;
	}

	/**
	 * Vrátí kořenový adresář - kanonickou verzi
	 */
	public File getRootFile() {
		return rootFile;
	}

	/**
	 * Vrátí aktuální adresář - jde o canonickou verzi názvu souboru, který byl
	 * předán konstruktoru
	 */
	public File getRequestedFile() {
		return requestedFile;
	}

	public File getTmpDirFile() {
		return tmpDirFile;
	}

}

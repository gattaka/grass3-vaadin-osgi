package cz.gattserver.grass3.fm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class FMExplorer {

	private Logger logger = LoggerFactory.getLogger(FMExplorer.class);

	private FileSystem fileSystem;

	/**
	 * Cesta ke kořeni FM úložiště
	 */
	private Path rootPath;

	/**
	 * Plná cesta od systémového kořene
	 */
	private Path currentAbsolutePath;

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
		/**
		 * V pořádku, nalezen
		 */
		SUCCESS,
		/**
		 * Nebyl nalezen
		 */
		MISSING,
		/**
		 * Existuje, ale podtéká kořenový adresář FM modulu, což je porušení
		 * security omezení
		 */
		NOT_VALID,
		/**
		 * Cílový soubor existuje, nelze vytvořit/přesunout/kopírovat
		 */
		ALREADY_EXISTS,
		/**
		 * Operace se nezdařila kvůli systémové chybě
		 */
		SYSTEM_ERROR
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
	public FMExplorer(String relativePath, FileSystem fileSystem) {
		this.fileSystem = fileSystem;
		processConfiguration();

		if (relativePath == null)
			relativePath = "";

		// Vytvoř File z předávané relativní cesty
		currentAbsolutePath = rootPath.resolve(relativePath).normalize();

		// Otestuj, zda File existuje, pokud ne, přesměruj se na kořenový
		// adresář a zapiš, že bylo nutné použít kořenový adresář kvůli
		// neexistenci předávaného souboru
		if (!Files.exists(currentAbsolutePath)) {
			this.currentAbsolutePath = rootPath;
			state = FileProcessState.MISSING;
		}

		// Zkontroluj validnost souboru, pokud není, přesměruj se na kořenový
		// adresář a zapiš, že bylo nutné použít kořenový adresář kvůli
		// nevalidnosti předávaného souboru
		if (!isValid(currentAbsolutePath)) {
			currentAbsolutePath = rootPath;
			state = FileProcessState.NOT_VALID;
		}
	}

	private void processConfiguration() {
		configuration = loadConfiguration();
		loadRootDirFromConfiguration(configuration);
	}

	private void loadRootDirFromConfiguration(FMConfiguration configuration) {
		String rootDir = configuration.getRootDir();
		rootPath = fileSystem.getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new GrassPageException(500, "Kořenový adresář FM modulu musí existovat");
		rootPath = rootPath.normalize();
	}

	/**
	 * Získá aktuální konfiguraci ze souboru konfigurace
	 * 
	 * @return soubor konfigurace FM
	 */
	private FMConfiguration loadConfiguration() {
		ConfigurationService configurationService = (ConfigurationService) SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	/**
	 * Ověří, že požadovaný adresář nepodtéká kořenový adresář
	 * 
	 * @throws IOException
	 *             pokud testovaný soubor neexistuje
	 */
	public boolean isValid(Path adeptPath) {
		return adeptPath.startsWith(rootPath);
	}

	/**
	 * Vytvoř nový adresář v aktuálním adresáři
	 * 
	 * @param name
	 *            jméno adresáře
	 * @return výsledek operace
	 */
	public FileProcessState createNewDir(String name) {
		Path newPath = currentAbsolutePath.resolve(name);
		try {
			if (!isValid(newPath))
				return FileProcessState.NOT_VALID;
			if (Files.exists(newPath))
				return FileProcessState.ALREADY_EXISTS;
			Files.createDirectory(newPath);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se vytvořit nový adresář {}", newPath.toString(), e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	public FileProcessState tryGotoDir(String name) {
		Path newPath = rootPath.resolve(name).normalize();
		if (isValid(newPath)) {
			currentAbsolutePath = newPath;
			return FileProcessState.SUCCESS;
		} else {
			return FileProcessState.NOT_VALID;
		}
	}

	/**
	 * Spočítá do hloubky velikost adresáře - pokud je mu předán soubor, který
	 * není adresář vrátí jeho velikost. MAX_VALUE Long je (2^63)-1, což je víc
	 * než 10^18, to je hodnota akorát velká pro pokrytí záznam Exbibajt (2^60)
	 * 
	 * @param path
	 *            adresář k spočítání
	 * @return velikost adresáře včetně podadresářů
	 * @throws IOException
	 *             pokud se nezdaří spočítat velikost souboru kvůli
	 *             {@link IOException} chybě
	 */
	public long getDeepDirSize(Path path) throws IOException {
		return innerGetDeepDirSize(path);
	}

	private long innerGetDeepDirSize(Path path) throws IOException {
		if (!Files.isDirectory(path))
			return Files.size(path);
		return Files.list(path).mapToLong(value -> {
			try {
				return innerGetDeepDirSize(value);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}).sum();
	}

	/**
	 * Uloží nahraný soubor
	 * 
	 * @param tmpFile
	 *            dočasný soubor
	 * @param filename
	 *            jeho název
	 * @return výsledek operace
	 */
	public FileProcessState saveFile(InputStream in, String filename) {
		Path path = currentAbsolutePath.resolve(filename);
		try {
			Files.copy(in, path);
		} catch (FileAlreadyExistsException f) {
			return FileProcessState.ALREADY_EXISTS;
		} catch (IOException e) {
			logger.error("Nezdařilo se vytvořit soubor {}", filename, e);
			return FileProcessState.SYSTEM_ERROR;
		}
		return FileProcessState.SUCCESS;
	}

	/**
	 * Smaže soubor
	 * 
	 * @param file
	 *            soubor
	 * @return výsledek operace
	 */
	public FileProcessState deleteFile(Path file) {
		try {
			if (!isValid(file))
				return FileProcessState.NOT_VALID;
			// TODO
			Files.delete(file);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se smazat soubor {}", file.toString(), e);
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
	public FileProcessState renameFile(Path path, String newName) {
		Path renamedPath = path.getParent().resolve(newName);
		try {
			if (!isValid(renamedPath))
				return FileProcessState.NOT_VALID;
			if (Files.exists(renamedPath))
				return FileProcessState.ALREADY_EXISTS;
			Files.move(path, renamedPath);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se přejmenovat soubor {} na {}", path, renamedPath, e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Vezme cestu k souboru a odstřihne od ní cestu k rootDir-u
	 * 
	 * @param path
	 *            soubor na zpracování
	 * @return koncová cesta k souboru od rootDir
	 */
	public String fileFromRoot(Path path) {
		return rootPath.relativize(path).toString();
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

	public Path getRootPath() {
		return rootPath;
	}

	public Path getCurrentAbsolutePath() {
		return currentAbsolutePath;
	}
	
	public Path getCurrentRelativePath() {
		return rootPath.relativize(currentAbsolutePath);
	}

	public Path getParentPath() {
		if (currentAbsolutePath.equals(rootPath))
			return currentAbsolutePath;
		return currentAbsolutePath.getParent();
	}

}

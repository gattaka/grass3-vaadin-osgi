package cz.gattserver.grass3.fm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.fm.interfaces.PathChunkTO;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class FMExplorer {

	private Logger logger = LoggerFactory.getLogger(FMExplorer.class);

	/**
	 * Filesystem, pod kterým {@link FMExplorer} momentálně operuje
	 */
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
	 * V jakém stavu je zpracovávaný soubor ?
	 */
	private FileProcessState state = FileProcessState.SUCCESS;

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
		loadRootDirFromConfiguration();

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

	private void loadRootDirFromConfiguration() {
		FMConfiguration configuration = loadConfiguration();
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
		ConfigurationService configurationService = SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);
		FMConfiguration c = new FMConfiguration();
		configurationService.loadConfiguration(c);
		return c;
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

	/**
	 * Změní aktuální adresář
	 * 
	 * @param name
	 *            jméno adresáře, do kterého přecházím. Může být ".." pro
	 *            vrácení se nahoru
	 * @return výsledek operace
	 */
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
	 * @return velikost adresáře včetně podadresářů nebo <code>null</code>,
	 *         pokud se jedná o nadřazený adresář
	 * @throws IOException
	 *             pokud se nezdaří spočítat velikost souboru kvůli
	 *             {@link IOException} chybě
	 */
	public Long getDeepDirSize(Path path) throws IOException {
		if (currentAbsolutePath.resolve(path).normalize().startsWith(currentAbsolutePath))
			return innerGetDeepDirSize(path);
		return null;
	}

	private Long innerGetDeepDirSize(Path path) throws IOException {
		if (!Files.isDirectory(path))
			return Files.size(path);
		try (Stream<Path> stream = Files.list(path)) {
			Long sum = 0L;
			for (Iterator<Path> it = stream.iterator(); it.hasNext();)
				sum += innerGetDeepDirSize(it.next());
			return sum;
		}
	}

	public int listCount() {
		try (Stream<Path> stream = Files.list(currentAbsolutePath)) {
			// +1 za odkaz na nadřazený adresář
			return (int) stream.count() + 1;
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
	}

	public Stream<Path> listing(int offset, int limit) {
		try {
			return Stream
					.concat(Stream.of(fileSystem.getPath("..")), Files.list(currentAbsolutePath).sorted((p1, p2) -> {
						if (Files.isDirectory(p1)) {
							if (Files.isDirectory(p2))
								return p1.getFileName().compareTo(p2);
							return -1;
						} else {
							if (Files.isDirectory(p2))
								return 1;
							return p1.getFileName().compareTo(p2);
						}
					})).skip(offset).limit(limit);
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
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

	/**
	 * Připraví dle aktuální cesty (od FM kořene) díly, ze kterých lze sestavit
	 * breadcrumb navigaci.
	 * 
	 * @return
	 */
	public List<PathChunkTO> getBreadcrumbChunks() {
		Path next = currentAbsolutePath;
		List<PathChunkTO> chunks = new ArrayList<>();
		do {
			String fileURLFromRoot = fileFromRoot(next);
			chunks.add(new PathChunkTO(next.equals(rootPath) ? "/" : next.getFileName().toString(), fileURLFromRoot));
			next = next.getParent();
			// pokud je můj předek null nebo jsem mimo povolený rozsah, pak
			// je to konec a je to všechno
		} while (next != null && next.startsWith(rootPath));
		return chunks;
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

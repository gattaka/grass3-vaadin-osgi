package cz.gattserver.grass3.fm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.fm.interfaces.FMItemTO;
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

	public static int sortFile(Path p1, Path p2) {
		if (Files.isDirectory(p1)) {
			if (Files.isDirectory(p2))
				return p1.getFileName().compareTo(p2);
			return -1;
		} else {
			if (Files.isDirectory(p2))
				return 1;
			return p1.getFileName().compareTo(p2);
		}
	}

	/**
	 * {@link FMExplorer} začne v adresáři, který je podle konfigurace jako jeho root (omezení).
	 * 
	 * @param fileSystem
	 *            {@link FileSystem}, ve kterém se bude {@link FMExplorer} pohybovat
	 */
	public FMExplorer(FileSystem fileSystem) {
		Validate.notNull(fileSystem, "Filesystem nesmí být null");

		this.fileSystem = fileSystem;
		loadRootDirFromConfiguration();

		currentAbsolutePath = rootPath;
	}

	private void loadRootDirFromConfiguration() {
		FMConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		rootPath = fileSystem.getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new GrassPageException(500, "Kořenový adresář FM modulu musí existovat");
		rootPath = rootPath.normalize();
	}

	private FMConfiguration loadConfiguration() {
		ConfigurationService configurationService = SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);
		FMConfiguration c = new FMConfiguration();
		configurationService.loadConfiguration(c);
		return c;
	}

	/**
	 * Změní aktuální adresář na adresář dle cesty od kořenového adresáře FM.
	 * 
	 * @param path
	 *            cesta k adresáři od kořenového adresáře FM
	 * @return výsledek operace
	 */
	public FileProcessState goToDir(String path) {
		return goToDir(rootPath.resolve(path).normalize());
	}

	/**
	 * Změní aktuální adresář na adresář dle cesty od aktuálního adresáře.
	 * 
	 * @param path
	 *            cesta k adresáři z aktuálního adresáře
	 * @return výsledek operace
	 */
	public FileProcessState goToDirFromCurrentDir(String path) {
		return goToDir(currentAbsolutePath.resolve(path).normalize());
	}

	private FileProcessState goToDir(Path path) {
		if (!isValid(path))
			return FileProcessState.NOT_VALID;
		if (!Files.exists(path))
			return FileProcessState.MISSING;
		if (!Files.isDirectory(path))
			return FileProcessState.DIRECTORY_REQUIRED;
		currentAbsolutePath = path;
		return FileProcessState.SUCCESS;
	}

	/**
	 * Ověří, že požadovaný adresář nepodtéká kořenový adresář
	 * 
	 * @throws IOException
	 *             pokud testovaný soubor neexistuje
	 */
	public boolean isValid(Path adeptPath) {
		return adeptPath.normalize().startsWith(rootPath);
	}

	/**
	 * Vytvoř nový adresář v aktuálním adresáři
	 * 
	 * @param path
	 *            cesta k souboru z aktuálního adresáře
	 * @return výsledek operace
	 */
	public FileProcessState createNewDir(String path) {
		Path newPath = currentAbsolutePath.resolve(path).normalize();
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

	private Long getFileSize(Path path) throws IOException {
		if (!Files.isDirectory(path))
			return Files.size(path);
		try (Stream<Path> stream = Files.list(path)) {
			Long sum = 0L;
			for (Iterator<Path> it = stream.iterator(); it.hasNext();)
				sum += getFileSize(it.next());
			return sum;
		}
	}

	/**
	 * Vrátí počet položek pro výpis obsahu aktuálního adresáře. Započítává i odkaz ".." na nadřazený adresář.
	 * 
	 * @return
	 */
	public int listCount() {
		try (Stream<Path> stream = Files.list(currentAbsolutePath)) {
			// +1 za odkaz na nadřazený adresář
			return (int) stream.count() + 1;
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
	}

	/**
	 * Vrátí {@link Stream} absolutních {@link Path} a na začátku ".." odkaz na nadřazený adresář.
	 * 
	 * @param offset
	 *            offset pro stránkování
	 * @param limit
	 *            velikost stránky
	 * @return
	 */
	public Stream<FMItemTO> listing(int offset, int limit) {
		try {
			return Stream
					.concat(Stream.of(currentAbsolutePath.resolve("..")),
							Files.list(currentAbsolutePath).sorted(FMExplorer::sortFile))
					.skip(offset).limit(limit).map(this::mapPathToItem);
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
	}

	private FMItemTO mapPathToItem(Path path) {
		FMItemTO to = new FMItemTO().setName(path.getFileName().toString());
		to.setDirectory(Files.isDirectory(path));
		try {
			to.setSize(path.normalize().startsWith(currentAbsolutePath)
					? HumanBytesSizeFormatter.format(getFileSize(path), true) : "");
		} catch (IOException e) {
			to.setSize("n/a");
		}
		try {
			to.setLastModified(
					LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
		} catch (IOException e) {
			to.setLastModified(null);
		}
		return to;
	}

	/**
	 * Uloží nahraný soubor
	 * 
	 * @param in
	 *            vstupní proud dat
	 * @param path
	 *            cesta k souboru z aktuálního adresáře pod kterou bude soubor uložen
	 * @return výsledek operace
	 */
	public FileProcessState saveFile(InputStream in, String path) {
		Path pathToSaveAs = currentAbsolutePath.resolve(path).normalize();
		try {
			Files.copy(in, pathToSaveAs);
		} catch (FileAlreadyExistsException f) {
			return FileProcessState.ALREADY_EXISTS;
		} catch (IOException e) {
			logger.error("Nezdařilo se vytvořit soubor {}", path, e);
			return FileProcessState.SYSTEM_ERROR;
		}
		return FileProcessState.SUCCESS;
	}

	/**
	 * Smaže soubor. Nelze smazat FM root.
	 * 
	 * @param path
	 *            cesta k souboru z aktuálního adresáře
	 * @return výsledek operace
	 */
	public FileProcessState deleteFile(String path) {
		Path pathToDelete = currentAbsolutePath.resolve(path).normalize();
		try {
			if (!isValid(pathToDelete) || rootPath.equals(pathToDelete))
				return FileProcessState.NOT_VALID;
			if (!Files.exists(pathToDelete))
				return FileProcessState.MISSING;
			try (Stream<Path> s = Files.walk(pathToDelete)) {
				s.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se smazat soubor {}", path, e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Přejmenuje soubor. Nelze přejmenovat FM root.
	 * 
	 * @param path
	 *            cesta ke stávajícímu souboru z aktuálního adresáře
	 * @param newPath
	 *            cesta k novému souboru z aktuálního adresáře -- umožňuje použít ".." a "/" pro přesun
	 */
	public FileProcessState renameFile(String path, String newPath) {
		Path currentPath = currentAbsolutePath.resolve(path).normalize();
		Path renamedPath = currentPath.getParent().resolve(newPath).normalize();
		try {
			if (!isValid(currentPath) || !isValid(renamedPath) || rootPath.equals(currentPath))
				return FileProcessState.NOT_VALID;
			if (!Files.exists(currentPath))
				return FileProcessState.MISSING;
			if (Files.exists(renamedPath))
				return FileProcessState.ALREADY_EXISTS;
			Files.move(currentPath, renamedPath);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se přejmenovat soubor {} na {}", path, newPath, e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Připraví dle aktuální cesty (od FM kořene) díly, ze kterých lze sestavit breadcrumb navigaci.
	 * 
	 * @return
	 */
	public List<FMItemTO> getBreadcrumbChunks() {
		Path next = currentAbsolutePath;
		List<FMItemTO> chunks = new ArrayList<>();
		do {
			String fileURLFromRoot = getPathFromRoot(next);
			chunks.add(new FMItemTO().setName(next.equals(rootPath) ? "/" : next.getFileName().toString())
					.setPathFromFMRoot(fileURLFromRoot));
			next = next.getParent();
			// pokud je můj předek null nebo jsem mimo povolený rozsah, pak
			// je to konec a je to všechno
		} while (next != null && next.startsWith(rootPath));
		return chunks;
	}

	private String getPathFromRoot(Path path) {
		return rootPath.relativize(path).toString();
	}

	private Path getCurrentRelativePath() {
		return rootPath.relativize(currentAbsolutePath);
	}

	/**
	 * Získá URL link k souboru v aktuálním adresáři. Neověřuje, zda soubor v aktuálním adresáři opravdu existuje, pouze
	 * sestavuje link tak, jako by v něm byl.
	 * 
	 * @param contextRootURL
	 *            kořenové url aplikace, například <code>http://testweb/grass</code>
	 * @param path
	 *            cesta k souboru z aktuálního adresáře
	 * @return link k souboru
	 */
	public String getDownloadLink(String contextRootURL, String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(contextRootURL);
		sb.append("/");
		sb.append(FMConfiguration.FM_PATH);
		for (Path part : getCurrentRelativePath()) {
			sb.append("/");
			sb.append(part.toString());
		}
		sb.append("/");
		sb.append(path);
		return sb.toString();
	}

	/**
	 * Získá URL pro aktuální stav FM. Pokud je tedy například FM v adresáři <code>alfa</code>, contextRoot aplikace je
	 * <code>http://testweb/grass</code> a URL cesta k FM modulu je <code>fm-modul</code>, pak výsledné URL bude <br/>
	 * 
	 * <code>http://testweb/grass/fm-modul/alfa</code>
	 * 
	 * @param contextRootURL
	 *            kořenové url aplikace, například <code>http://testweb/grass</code>
	 * @param modulePageName
	 *            URL cesta k FM modulu, například <code>fm-modul</code>
	 * @return výsledné URL k aktuálnímu adresáři
	 */
	public String getCurrentURL(String contextRootURL, String modulePageName) {
		StringBuilder sb = new StringBuilder();
		sb.append(contextRootURL);
		sb.append("/");
		sb.append(modulePageName);
		for (Path part : getCurrentRelativePath()) {
			sb.append("/");
			try {
				sb.append(URLEncoder.encode(part.toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param contextRootURL
	 *            kořenové url aplikace, například <code>http://testweb/grass</code>
	 * @param modulePageName
	 *            URL cesta k FM modulu, například <code>fm-modul</code>
	 * @param uri
	 *            URL, dle kterého se má získat adresář, kam se mám přepnout
	 * @return výsledek operace
	 */
	public FileProcessState goToDirByURL(String contextRootURL, String modulePageName, String uri) {
		// Odparsuj počátek http://host//context-root/fm a získej
		// lokální cestu v rámci FM modulu
		int start = uri.indexOf(contextRootURL);
		String fmPath = uri.substring(start + contextRootURL.length() + 1 + modulePageName.length());
		if (fmPath.isEmpty() || fmPath.startsWith("/")) {
			if (fmPath.startsWith("/"))
				fmPath = fmPath.substring(1);
			return goToDir(fmPath);
		} else {
			// úplně jiná stránka, která akorát začíná na
			// "context-root/fm"
			return FileProcessState.SYSTEM_ERROR;
		}
	}

}

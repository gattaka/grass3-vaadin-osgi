package cz.gattserver.grass3.print3d.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.exception.GrassException;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.modules.Print3dModule;
import cz.gattserver.grass3.print3d.config.Print3dConfiguration;
import cz.gattserver.grass3.print3d.events.impl.Print3dZipProcessProgressEvent;
import cz.gattserver.grass3.print3d.events.impl.Print3dZipProcessResultEvent;
import cz.gattserver.grass3.print3d.events.impl.Print3dZipProcessStartEvent;
import cz.gattserver.grass3.print3d.interfaces.Print3dPayloadTO;
import cz.gattserver.grass3.print3d.interfaces.Print3dTO;
import cz.gattserver.grass3.print3d.interfaces.Print3dViewItemTO;
import cz.gattserver.grass3.print3d.model.domain.Print3d;
import cz.gattserver.grass3.print3d.model.repositories.Print3dRepository;
import cz.gattserver.grass3.print3d.service.Print3dService;
import cz.gattserver.grass3.print3d.util.Print3dMapper;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.FileSystemService;

@Transactional
@Service
public class Print3dServiceImpl implements Print3dService {

	private static Logger logger = LoggerFactory.getLogger(Print3dServiceImpl.class);

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private Print3dMapper projectMapper;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private Print3dRepository print3dRepository;

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private EventBus eventBus;

	@Override
	public Print3dConfiguration loadConfiguration() {
		Print3dConfiguration configuration = new Print3dConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(Print3dConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

	private void deleteFileRecursively(Path file) throws IOException {
		if (Files.isDirectory(file)) {
			try (Stream<Path> stream = Files.list(file)) {
				Iterator<Path> it = stream.iterator();
				while (it.hasNext())
					deleteFileRecursively(it.next());
			}
		}
		Files.delete(file);
	}

	@Override
	public boolean deleteProject(long id) {
		String path = print3dRepository.findPhotogalleryPathById(id);
		Path dir = getProjectPath(path);

		print3dRepository.deleteById(id);
		contentNodeFacade.deleteByContentId(Print3dModule.ID, id);

		// musí se řešit return stavem, protože exception by způsobilo rollback
		// transakce, což nechci
		try {
			deleteFileRecursively(dir);
			return true;
		} catch (Exception e) {
			logger.error("Nezdařilo se smazat některé soubory: " + id, e);
			return false;
		}
	}

	@Override
	public void modifyProject(long projectId, Print3dPayloadTO payloadTO) {
		innerSaveProject(payloadTO, projectId, null, null);
	}

	@Override
	public Long saveProject(Print3dPayloadTO payloadTO, long nodeId, long authorId) {
		return innerSaveProject(payloadTO, null, nodeId, authorId);
	}

	private Print3d saveProject(String projectDir, Print3dPayloadTO payloadTO, Long existingId, Long nodeId,
			Long authorId) {

		Print3d project = existingId == null ? new Print3d() : print3dRepository.findById(existingId).orElse(null);

		// nasetuj do ní vše potřebné
		project.setProjectPath(projectDir);

		// ulož ho a nasetuj jeho id
		project = print3dRepository.save(project);
		if (project == null)
			return null;

		if (existingId == null) {
			// vytvoř odpovídající content node
			Long contentNodeId = contentNodeFacade.save(Print3dModule.ID, project.getId(), payloadTO.getName(),
					payloadTO.getTags(), payloadTO.isPublicated(), nodeId, authorId, false,
					project.getContentNode().getCreationDate(), null);

			// ulož do článku referenci na jeho contentnode
			ContentNode contentNode = new ContentNode();
			contentNode.setId(contentNodeId);
			project.setContentNode(contentNode);
			if (print3dRepository.save(project) == null)
				return null;
		} else {
			contentNodeFacade.modify(project.getContentNode().getId(), payloadTO.getName(), payloadTO.getTags(),
					payloadTO.isPublicated(), LocalDateTime.now());
		}

		return project;
	}

	private Long innerSaveProject(Print3dPayloadTO payloadTO, Long existingId, Long nodeId, Long authorId) {
		String galleryDir = payloadTO.getProjectDir();
		Path galleryPath = getProjectPath(galleryDir);
		try (Stream<Path> stream = Files.list(galleryPath).sorted(getComparator())) {
			Print3d print3d = saveProject(galleryDir, payloadTO, existingId, nodeId, authorId);
			return print3d.getId();
		} catch (IOException e) {
			String msg = "Nezdařilo se uložit galerii";
			logger.error(msg, e);
			throw new GrassException(msg, e);
		}
	}

	@Override
	public String createProjectDir() throws IOException {
		Print3dConfiguration configuration = loadConfiguration();
		String dirRoot = configuration.getRootDir();
		Path dirRootFile = fileSystemService.getFileSystem().getPath(dirRoot);
		long systime = System.currentTimeMillis();
		Path tmpDirFile = dirRootFile.resolve("pgGal_" + systime);
		Files.createDirectories(tmpDirFile);
		return tmpDirFile.getFileName().toString();
	}

	@Override
	public Print3dTO getProjectForDetail(Long id) {
		Validate.notNull(id, "Id nesmí být null");
		Print3d project = print3dRepository.findById(id).orElse(null);
		if (project == null)
			return null;
		return projectMapper.mapProjectForDetail(project);
	}

	/**
	 * Získá {@link Path} dle jméno adresáře projektu
	 * 
	 * @param projectDir
	 *            jméno adresáře projektu
	 * @return {@link Path} objekt projektu
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář projektů -- chyba nastavení
	 *             modulu Print3d
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu Print3d
	 */
	private Path getProjectPath(String projectDir) {
		Print3dConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath)) {
			IllegalStateException ise = new IllegalStateException("Kořenový adresář Print3d modulu musí existovat");
			logger.error("Nezdařilo se získat kořenový adresář", ise);
			throw ise;
		}
		rootPath = rootPath.normalize();
		Path galleryPath = rootPath.resolve(projectDir);
		if (!galleryPath.normalize().startsWith(rootPath)) {
			IllegalArgumentException ise = new IllegalArgumentException("Podtečení kořenového adresáře projektů");
			logger.error("Nezdařilo se získat kořenový adresář projektu", ise);
			throw ise;
		}
		return galleryPath;
	}

	@Async
	@Override
	public void zipProject(String projectDir) {
		Path projectPath = getProjectPath(projectDir);

		logger.info("zip3dProject thread: " + Thread.currentThread().getId());

		final ReferenceHolder<Integer> total = new ReferenceHolder<>();
		final ReferenceHolder<Integer> progress = new ReferenceHolder<>();

		try (Stream<Path> stream = Files.list(projectPath)) {
			total.setValue((int) stream.count());
			eventBus.publish(new Print3dZipProcessStartEvent(total.getValue() + 1));
		} catch (Exception e) {
			String msg = "Nezdařilo se získat počet souborů ke komprimaci";
			eventBus.publish(new Print3dZipProcessResultEvent(msg, e));
			logger.error(msg, e);
			return;
		}

		progress.setValue(1);

		String zipFileName = "grassPrint3dTmpFile-" + new Date().getTime() + "-" + projectDir + ".zip";
		try {
			Path zipFile = fileSystemService.createTmpDir("grassPrint3dTmpFolder").resolve(zipFileName);
			try (FileSystem zipFileSystem = fileSystemService.newZipFileSystem(zipFile, true)) {
				performZip(projectPath, zipFileSystem, progress, total);
				eventBus.publish(new Print3dZipProcessResultEvent(zipFile));
			} catch (Exception e) {
				String msg = "Nezdařilo se vytvořit ZIP projektu";
				eventBus.publish(new Print3dZipProcessResultEvent(msg, e));
				logger.error(msg, e);
			}
		} catch (Exception e) {
			String msg = "Nezdařilo se vytvořit dočasný adresář pro ZIP projektu";
			eventBus.publish(new Print3dZipProcessResultEvent(msg, e));
			logger.error(msg, e);
		}
	}

	private void performZip(Path galleryPath, FileSystem zipFileSystem, ReferenceHolder<Integer> progress,
			ReferenceHolder<Integer> total) throws IOException {
		final Path root = zipFileSystem.getRootDirectories().iterator().next();
		try (Stream<Path> stream = Files.list(galleryPath)) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path src = it.next();
				eventBus.publish(new Print3dZipProcessProgressEvent("Přidávám '" + src.getFileName() + "' do ZIPu "
						+ progress.getValue() + "/" + total.getValue()));
				progress.setValue(progress.getValue() + 1);
				Path dest = root.resolve(src.getFileName().toString());
				Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	@Override
	public List<Print3dViewItemTO> deleteFiles(Set<Print3dViewItemTO> selected, String galleryDir) {
		List<Print3dViewItemTO> removed = new ArrayList<>();
		for (Print3dViewItemTO itemTO : selected) {
			deleteFile(itemTO, galleryDir);
			removed.add(itemTO);
		}
		return removed;
	}

	@Override
	public void deleteFile(Print3dViewItemTO itemTO, String galleryDir) {
		Path galleryPath = getProjectPath(galleryDir);
		Path subFile = galleryPath.resolve(itemTO.getFile());
		if (Files.exists(subFile)) {
			try {
				Files.delete(subFile);
			} catch (Exception e) {
				logger.error("Nezdařilo se smazat soubor {}", subFile, e);
			}
		} else {
			logger.info("Nezdařilo se najít soubor {}", subFile);
		}
	}

	@Override
	public void uploadFile(InputStream in, String fileName, String galleryDir) throws IOException {
		Path galleryPath = getProjectPath(galleryDir);
		Path filePath = galleryPath.resolve(fileName);
		if (!filePath.normalize().startsWith(galleryPath))
			throw new IllegalArgumentException("Podtečení adresáře galerie");
		Files.copy(in, filePath);
	}

	@Override
	public List<Print3dViewItemTO> getItems(String galleryDir) throws IOException {
		Path galleryPath = getProjectPath(galleryDir);
		List<Print3dViewItemTO> items = new ArrayList<>();
		try (Stream<Path> stream = Files.list(galleryPath).sorted(getComparator())) {
			stream.filter(file -> !Files.isDirectory(file)).forEach(file -> {
				Print3dViewItemTO itemTO = new Print3dViewItemTO();
				itemTO.setName(file.getFileName().toString());
				items.add(itemTO);
			});

		}
		return items;
	}

	@Override
	public int getViewItemsCount(String galleryDir) throws IOException {
		Path galleryPath = getProjectPath(galleryDir);
		try (Stream<Path> stream = Files.list(galleryPath)) {
			return (int) stream.filter(file -> !Files.isDirectory(file)).count();
		}
	}

	private Comparator<Path> getComparator() {
		Comparator<Path> nameComparator = (p1, p2) -> p1.getFileName().toString()
				.compareTo(p2.getFileName().toString());
		Comparator<Path> comparator = (p1, p2) -> {
			try {
				return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return nameComparator.compare(p1, p2);
		};
		return comparator;
	}

	@Override
	public List<Print3dViewItemTO> getViewItems(String galleryDir, int skip, int limit) throws IOException {
		Path galleryPath = getProjectPath(galleryDir);
		List<Print3dViewItemTO> list = new ArrayList<>();

		try (Stream<Path> filesStream = Files.list(galleryPath).sorted(getComparator())) {
			filesStream.skip(skip).limit(limit).forEach(file -> {
				Print3dViewItemTO itemTO = new Print3dViewItemTO();
				String fileName = file.getFileName().toString();
				itemTO.setName(fileName);
				itemTO.setFile(file);
				list.add(itemTO);
			});
		}
		return list;
	}

	@Override
	public void deleteZipFile(Path zipFile) {
		try {
			Files.delete(zipFile);
		} catch (IOException e) {
			logger.error("Nezdařilo se smazat ZIP soubor {}", zipFile.getFileName().toString());
		}
	}

	@Override
	public void deleteDraft(String projectDir) throws IOException {
		Path projectPath = getProjectPath(projectDir);
		Files.walkFileTree(projectPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try {
					Files.delete(file);
				} catch (Exception e) {
					logger.error("Nezdařilo se smazat soubor zrušeného rozpracovaného projektu {}",
							file.getFileName().toString(), e);
				}
				return FileVisitResult.CONTINUE;
			}
		});
		Files.delete(projectPath);
	}

	@Override
	public Path getFullImage(String galleryDir, String file) {
		Path galleryPath = getProjectPath(galleryDir);
		Path filePath = galleryPath.resolve(file);
		if (!filePath.normalize().startsWith(galleryPath))
			throw new IllegalArgumentException("Podtečení adresáře projektu");
		return filePath;
	}

	@Override
	public boolean checkProject(String galleryDir) {
		Path galleryPath = getProjectPath(galleryDir);
		return Files.exists(galleryPath);
	}
}

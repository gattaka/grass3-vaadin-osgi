package cz.gattserver.grass3.pg.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.modules.PGModule;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGZipProcessStartEvent;
import cz.gattserver.grass3.pg.exception.UnauthorizedAccessException;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;
import cz.gattserver.grass3.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.util.DecodeAndCaptureFrames;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.pg.util.PhotogalleryMapper;
import cz.gattserver.grass3.pg.util.ZIPUtils;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.web.common.spring.SpringContextHelper;

@Transactional
@Component
public class PGServiceImpl implements PGService {

	private static Logger logger = LoggerFactory.getLogger(PGServiceImpl.class);

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private PhotogalleryMapper photogalleriesMapper;

	@Autowired
	private SecurityService securityFacade;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private PhotogalleryRepository photogalleryRepository;

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private EventBus eventBus;

	@Override
	public PGConfiguration getConfiguration() {
		PGConfiguration configuration = new PGConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(PGConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

	private void deleteFileRecursively(Path file) {
		try (Stream<Path> stream = Files.list(file)) {
			stream.forEach(this::deleteFileRecursively);
			Files.delete(file);
		} catch (Exception e) {
			throw new IllegalStateException("Nelze smazat soubor '" + file.getFileName().toString() + "'");
		}
	}

	@Override
	public void deletePhotogallery(long photogalleryId) {
		String path = photogalleryRepository.findPhotogalleryPathById(photogalleryId);
		Path galleryDir = getGalleryDir(path);
		deleteFileRecursively(galleryDir);

		photogalleryRepository.delete(photogalleryId);
		contentNodeFacade.deleteByContentId(PGModule.ID, photogalleryId);
	}

	private void createVideoMinature(Path file, Path outputFile) {
		String videoName = outputFile.getFileName().toString();
		try {
			logger.info("Bylo nalezeno video {}", videoName);
			logger.info("Bylo zahájeno zpracování náhledu videa {}", videoName);
			BufferedImage image = new DecodeAndCaptureFrames().decodeAndCaptureFrames(file);
			logger.info("Zpracování náhledu videa {} byla úspěšně dokončeno", videoName);
			image = PGUtils.resizeBufferedImage(image, 150, 150);
			ImageIO.write(image, "png", outputFile.toFile());
			logger.info("Náhled videa {} byl úspěšně uložen", videoName);
		} catch (Exception e) {
			logger.error("Vytváření náhledu videa {} se nezdařilo", videoName, e);
		}
	}

	private void createImageMinature(Path file, Path outputFile) {
		String imageName = outputFile.getFileName().toString();
		try {
			PGUtils.resizeAndRotateImageFile(file, outputFile, 150, 150);
			logger.info("Náhled obrázku {} byl úspěšně uložen", imageName);
		} catch (Exception e) {
			logger.error("Vytváření náhledu obrázku {} se nezdařilo", imageName, e);
		}
	}

	private void processMiniatureImages(Photogallery photogallery) throws IOException {
		PGConfiguration configuration = getConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		String previewsDir = configuration.getPreviewsDir();
		Path galleryDir = getGalleryDir(photogallery);

		int total = 0;
		try (Stream<Path> stream = Files.list(galleryDir)) {
			total = (int) stream.count();
		}

		int progress = 1;

		Path miniDirFile = galleryDir.resolve(miniaturesDir);
		if (!Files.exists(miniDirFile))
			Files.createDirectories(miniDirFile);

		Path prevDirFile = galleryDir.resolve(previewsDir);
		if (!Files.exists(prevDirFile))
			Files.createDirectories(prevDirFile);

		try (Stream<Path> stream = Files.list(galleryDir)) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path file = it.next();
				// pokud bych miniaturizoval adresář přeskoč
				if (Files.isDirectory(file))
					continue;

				boolean videoExt = PGUtils.isVideo(file);
				boolean imageExt = PGUtils.isImage(file);

				if (!videoExt && !imageExt)
					continue;

				// soubor miniatury
				Path outputFile;
				if (videoExt)
					outputFile = prevDirFile.resolve(file.getFileName().toString() + ".png");
				else
					outputFile = miniDirFile.resolve(file.getFileName().toString());

				eventBus.publish(new PGProcessProgressEvent("Zpracování miniatur " + progress + "/" + total));
				progress++;

				// už existuje? ok, není potřeba znovu vytvářet
				if (!Files.exists(outputFile)) {
					if (videoExt)
						createVideoMinature(file, outputFile);
					else
						createImageMinature(file, outputFile);
				}
			}
		}
	}

	private void processSlideshowImages(Photogallery photogallery) throws IOException {
		PGConfiguration configuration = getConfiguration();
		String slideshowDir = configuration.getSlideshowDir();
		Path galleryDir = getGalleryDir(photogallery);

		int total = 0;
		try (Stream<Path> stream = Files.list(galleryDir)) {
			total = (int) stream.count();
		}

		int progress = 1;

		Path slideshowDirFile = galleryDir.resolve(slideshowDir);
		if (!Files.exists(slideshowDirFile))
			Files.createDirectories(slideshowDirFile);

		try (Stream<Path> stream = Files.list(galleryDir)) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path file = it.next();
				Path outputFile = slideshowDirFile.resolve(file);

				if (Files.exists(outputFile) || Files.isDirectory(file) || !PGUtils.isImage(file))
					continue;

				eventBus.publish(new PGProcessProgressEvent("Zpracování slideshow " + progress + "/" + total));
				progress++;

				// vytvoř slideshow verzi
				BufferedImage image = ImageIO.read(file.toFile());
				if (image.getWidth() > 900 || image.getHeight() > 700) {
					try {
						PGUtils.resizeAndRotateImageFile(file, outputFile, 900, 700);
					} catch (Exception e) {
						logger.error("Při zpracování slideshow pro '{}' došlo k chybě", file.getFileName().toString(),
								e);
					}
				}
			}
		}
	}

	@Override
	@Async
	public void modifyPhotogallery(long photogalleryId, PhotogalleryPayloadTO payloadTO, LocalDateTime date) {
		innerSavePhotogallery(payloadTO, photogalleryId, null, null, date);
	}

	@Override
	@Async
	public void savePhotogallery(PhotogalleryPayloadTO payloadTO, long nodeId, long authorId, LocalDateTime date) {
		innerSavePhotogallery(payloadTO, null, nodeId, authorId, date);
	}

	private void publishPGProcessFailure() {
		eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
	}
	
	private void innerSavePhotogallery(PhotogalleryPayloadTO payloadTO, Long existingId, Long nodeId, Long authorId,
			LocalDateTime date) {
		try (Stream<Path> stream = Files.list(payloadTO.getGalleryDir())) {
			logger.info("modifyPhotogallery thread: " + Thread.currentThread().getId());

			// Počet kroků = miniatury + detaily + uložení
			int total = (int) stream.count();
			eventBus.publish(new PGProcessStartEvent(2 * total + 1));

			Photogallery photogallery = existingId == null ? new Photogallery()
					: photogalleryRepository.findOne(existingId);

			// nasetuj do ní vše potřebné
			photogallery.setPhotogalleryPath(payloadTO.getGalleryDir().getFileName().toString());

			// ulož ho a nasetuj jeho id
			photogallery = photogalleryRepository.save(photogallery);
			if (photogallery == null) {
				publishPGProcessFailure();
				return;
			}

			if (existingId == null) {
				// vytvoř odpovídající content node
				Long contentNodeId = contentNodeFacade.save(PGModule.ID, photogallery.getId(), payloadTO.getName(),
						payloadTO.getTags(), payloadTO.isPublicated(), nodeId, authorId, false, date, null);

				// ulož do článku referenci na jeho contentnode
				ContentNode contentNode = new ContentNode();
				contentNode.setId(contentNodeId);
				photogallery.setContentNode(contentNode);
				if (photogalleryRepository.save(photogallery) == null) {
					publishPGProcessFailure();
					return;
				}
			} else {
				contentNodeFacade.modify(photogallery.getContentNode().getId(), payloadTO.getName(),
						payloadTO.getTags(), payloadTO.isPublicated(), date);
			}

			// vytvoř miniatury
			processMiniatureImages(photogallery);

			// vytvoř detaily
			processSlideshowImages(photogallery);

			// ulož ho a nasetuj jeho id
			photogallery = photogalleryRepository.save(photogallery);
			if (photogallery == null) {
				publishPGProcessFailure();
				return;
			}

			// vytvoř odpovídající content node
			eventBus.publish(new PGProcessProgressEvent("Uložení obsahu galerie"));
			Long contentNodeId = contentNodeFacade.save(PGModule.ID, photogallery.getId(), payloadTO.getName(),
					payloadTO.getTags(), payloadTO.isPublicated(), nodeId, authorId, false, date, null);

			// ulož do galerie referenci na její contentnode
			ContentNode contentNode = new ContentNode();
			contentNode.setId(contentNodeId);
			photogallery.setContentNode(contentNode);
			if (photogalleryRepository.save(photogallery) == null) {
				publishPGProcessFailure();
				return;
			}

			eventBus.publish(new PGProcessResultEvent(photogallery.getId()));
		} catch (Exception e) {
			publishPGProcessFailure();
			logger.error("Nezdařilo se uložit galerii", e);
			return;
		}
	}

	@Override
	public Path createGalleryDir() {
		PGConfiguration configuration = getConfiguration();
		String dirRoot = configuration.getRootDir();
		Path dirRootFile = fileSystemService.getFileSystem().getPath(dirRoot);

		long systime = System.currentTimeMillis();

		for (int i = 0; i < 10000; i++) {
			Path tmpDirFile = dirRootFile.resolve(String.valueOf(systime) + "_" + i);
			try {
				Files.createDirectories(tmpDirFile);
			} catch (FileAlreadyExistsException e) {
				continue;
			} catch (Exception e) {

			}
			// zdařilo se - vracím jméno tmp adresáře
			return tmpDirFile;
		}

		// ani na 10000 pokusů se nepodařilo vytvořit nekonfliktní adresář
		return null;
	}

	@Override
	public PhotogalleryTO getPhotogalleryForDetail(long id) {
		Photogallery photogallery = photogalleryRepository.findOne(id);
		if (photogallery == null)
			return null;
		PhotogalleryTO photogalleryDTO = photogalleriesMapper.mapPhotogalleryForDetail(photogallery);
		return photogalleryDTO;
	}

	@Override
	public List<PhotogalleryTO> getAllPhotogalleriesForSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getGalleryDir(PhotogalleryTO photogallery) {
		return getGalleryDir(photogallery.getPhotogalleryPath());
	}

	private Path getGalleryDir(Photogallery photogallery) {
		return getGalleryDir(photogallery.getPhotogalleryPath());
	}

	private Path getGalleryDir(String photogalleryPath) {
		PGConfiguration configuration = getConfiguration();
		return fileSystemService.getFileSystem().getPath(configuration.getRootDir(), photogalleryPath);
	}

	@Override
	public void tryDeleteMiniatureImage(String file, PhotogalleryTO photogalleryDTO) {
		deleteGalleryFile(file, photogalleryDTO, getConfiguration().getMiniaturesDir());
	}

	@Override
	public void tryDeletePreviewImage(String file, PhotogalleryTO photogalleryDTO) {
		deleteGalleryFile(file, photogalleryDTO, getConfiguration().getPreviewsDir());
	}

	@Override
	public void tryDeleteSlideshowImage(String file, PhotogalleryTO photogalleryDTO) {
		deleteGalleryFile(file, photogalleryDTO, getConfiguration().getSlideshowDir());
	}

	private void deleteGalleryFile(String file, PhotogalleryTO photogalleryDTO, String subDir) {
		Path galleryDir = getGalleryDir(photogalleryDTO);
		Path miniDirFile = galleryDir.resolve(subDir);
		Path miniFile = miniDirFile.resolve(file);
		if (Files.exists(miniFile))
			try {
				Files.delete(miniFile);
			} catch (IOException e) {
				throw new IllegalStateException("Nelze smazat soubor '" + file + "'");
			}
	}

	@Override
	public List<PhotogalleryRESTOverviewTO> getAllPhotogalleriesForREST(Long userId) {
		return photogalleriesMapper
				.mapPhotogalleryForRESTOverviewCollection(photogalleryRepository.findByUserAccess(userId));
	}

	@Override
	public PhotogalleryRESTTO getPhotogalleryForREST(Long id) throws UnauthorizedAccessException {
		Photogallery gallery = photogalleryRepository.findOne(id);
		if (gallery == null)
			return null;

		UserInfoTO user = securityFacade.getCurrentUser();
		if (gallery.getContentNode().getPublicated() || user.isAdmin()
				|| gallery.getContentNode().getAuthor().getId().equals(user.getId())) {

			PGConfiguration configuration = getConfiguration();
			Path file = fileSystemService.getFileSystem().getPath(configuration.getRootDir(),
					gallery.getPhotogalleryPath());
			if (Files.exists(file)) {
				Set<String> files = new HashSet<>();
				try (Stream<Path> stream = Files.list(file)) {
					stream.filter(f -> !Files.isDirectory(f)).forEach(f -> files.add(f.getFileName().toString()));
				} catch (IOException e) {
					throw new IllegalStateException(
							"Nelze získat přehled souborů z '" + file.getFileName().toString() + "'");
				}
				PhotogalleryRESTTO photogalleryDTO = new PhotogalleryRESTTO(gallery.getId(),
						gallery.getContentNode().getName(), gallery.getContentNode().getCreationDate(),
						gallery.getContentNode().getLastModificationDate(),
						gallery.getContentNode().getAuthor().getName(), files);
				return photogalleryDTO;
			} else {
				return null;
			}
		}
		throw new UnauthorizedAccessException();
	}

	@Override
	public Path getPhotoForREST(Long id, String fileName, boolean mini) throws UnauthorizedAccessException {
		Photogallery gallery = photogalleryRepository.findOne(id);
		if (gallery == null)
			return null;

		UserInfoTO user = securityFacade.getCurrentUser();
		if (gallery.getContentNode().getPublicated() || user.isAdmin()
				|| gallery.getContentNode().getAuthor().getId().equals(user.getId())) {
			PGConfiguration configuration = loadConfiguration();
			Path rootPath = loadRootDirFromConfiguration(configuration);
			Path galleryPath = rootPath.resolve(gallery.getPhotogalleryPath());
			Path miniaturesPath = galleryPath.resolve(configuration.getMiniaturesDir());
			Path slideshowPath = galleryPath.resolve(configuration.getSlideshowDir());
			Path file = mini ? miniaturesPath.resolve(fileName) : slideshowPath.resolve(fileName);
			if (Files.exists(file)) {
				return file;
			} else {
				if (!mini) {
					// pokud jsem nenašel slideshow, je to tak malý obrázek, že
					// postačí originální velikost a nemá vytvořený slideshow
					file = galleryPath.resolve(fileName);
					if (Files.exists(file))
						return file;
				}
				// pokud jsem nenašel miniaturu, je potřeba ji vytvořit
			}
			return null;
		}

		throw new UnauthorizedAccessException();
	}

	private Path loadRootDirFromConfiguration(PGConfiguration configuration) {
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new GrassPageException(500, "Kořenový adresář PG modulu musí existovat");
		rootPath = rootPath.normalize();
		return rootPath;
	}

	private PGConfiguration loadConfiguration() {
		ConfigurationService configurationService = SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);
		PGConfiguration c = new PGConfiguration();
		configurationService.loadConfiguration(c);
		return c;
	}

	@Async
	@Override
	public void zipGallery(Path galleryDir) {
		Path rootPath = loadRootDirFromConfiguration(loadConfiguration());
		Path galleryPath = rootPath.resolve(galleryDir);

		logger.info("zipPhotogallery thread: " + Thread.currentThread().getId());

		final ReferenceHolder<Integer> total = new ReferenceHolder<>();
		final ReferenceHolder<Integer> progress = new ReferenceHolder<>();

		try (Stream<Path> stream = Files.list(galleryPath)) {
			total.setValue((int) stream.count());
			eventBus.publish(new PGZipProcessStartEvent(total.getValue() + 1));
		} catch (Exception e) {
			eventBus.publish(new PGZipProcessResultEvent(false, "Nezdařilo se získat počet souborů ke komprimaci"));
			return;
		}

		progress.setValue(1);

		String zipFileName = "grassPGTmpFile_" + new Date().getTime() + "_" + galleryDir;
		Path zipFile = fileSystemService.getFileSystem().getPath(zipFileName);

		try (FileSystem zipFileSystem = ZIPUtils.createZipFileSystem(zipFile, true)) {
			final Path root = zipFileSystem.getPath("/");

			try (Stream<Path> stream = Files.list(galleryPath)) {

				Iterator<Path> it = stream.iterator();
				while (it.hasNext()) {
					Path src = it.next();
					eventBus.publish(new PGZipProcessProgressEvent(
							"Přidávám '" + src + "' do ZIPu " + progress.getValue() + "/" + total.getValue()));
					progress.setValue(progress.getValue() + 1);

					// add a file to the zip file system
					if (!Files.isDirectory(src)) {
						final Path dest = zipFileSystem.getPath(root.toString(), src.toString());
						final Path parent = dest.getParent();
						if (Files.notExists(parent)) {
							System.out.printf("Creating directory %s\n", parent);
							Files.createDirectories(parent);
						}
						Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
					} else {
						// for directories, walk the file tree
						Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								final Path dest = zipFileSystem.getPath(root.toString(), file.toString());
								Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
									throws IOException {
								final Path dirToCreate = zipFileSystem.getPath(root.toString(), dir.toString());
								if (Files.notExists(dirToCreate)) {
									System.out.printf("Creating directory %s\n", dirToCreate);
									Files.createDirectories(dirToCreate);
								}
								return FileVisitResult.CONTINUE;
							}
						});
					}

				}
			} catch (Exception e) {
				eventBus.publish(new PGZipProcessResultEvent(false, "Nezdařilo se vytvořit ZIP galerie"));
				logger.error("Nezdařilo se vytvořit ZIP galerie", e);
			}

			eventBus.publish(new PGZipProcessResultEvent(zipFile));

		} catch (Exception e) {
			eventBus.publish(new PGZipProcessResultEvent(false, "Nezdařilo se vytvořit ZIP galerie"));
			logger.error("Nezdařilo se vytvořit ZIP galerie", e);
		}
	}

}

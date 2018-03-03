package cz.gattserver.grass3.pg.service.impl;

import java.awt.image.BufferedImage;
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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
import cz.gattserver.grass3.pg.interfaces.PhotogalleryItemType;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;
import cz.gattserver.grass3.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.util.DecodeAndCaptureFrames;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.pg.util.PhotogalleryMapper;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.services.SecurityService;

@Transactional
@Service
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

	private enum GalleryFileType {
		MAIN_FILE, PREVIEW, SLIDESHOW, MINIATURE,
	}

	@Override
	public PGConfiguration loadConfiguration() {
		PGConfiguration configuration = new PGConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(PGConfiguration configuration) {
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
	public void deletePhotogallery(long photogalleryId) throws IOException {
		String path = photogalleryRepository.findPhotogalleryPathById(photogalleryId);
		Path galleryDir = getGalleryPath(path);
		deleteFileRecursively(galleryDir);

		photogalleryRepository.delete(photogalleryId);
		contentNodeFacade.deleteByContentId(PGModule.ID, photogalleryId);
	}

	private void createVideoMinature(Path file, Path outputFile) {
		String videoName = file.getFileName().toString();
		String previewName = outputFile.getFileName().toString();
		try {
			logger.info("Bylo nalezeno video {}", videoName);
			logger.info("Bylo zahájeno zpracování náhledu videa {}", videoName);
			BufferedImage image = new DecodeAndCaptureFrames().decodeAndCaptureFrames(file);
			logger.info("Zpracování náhledu videa {} byla úspěšně dokončeno", videoName);
			PGUtils.resizeVideoPreviewImage(image, outputFile);
			logger.info("Náhled videa {} byl úspěšně uložen", previewName);
		} catch (Exception e) {
			logger.error("Vytváření náhledu videa {} se nezdařilo", videoName, e);
		}
	}

	private void createImageMinature(Path file, Path outputFile) {
		String imageName = outputFile.getFileName().toString();
		try {
			PGUtils.resizeImage(file, outputFile);
			logger.info("Náhled obrázku {} byl úspěšně uložen", imageName);
		} catch (Exception e) {
			logger.error("Vytváření náhledu obrázku {} se nezdařilo", imageName, e);
		}
	}

	private void processMiniatureImages(Photogallery photogallery) throws IOException {
		PGConfiguration configuration = loadConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		String previewsDir = configuration.getPreviewsDir();
		Path galleryDir = getGalleryPath(photogallery.getPhotogalleryPath());

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

				eventBus.publish(new PGProcessProgressEvent("Zpracování miniatur " + progress + "/" + total));
				progress++;

				// pokud bych miniaturizoval adresář přeskoč
				if (Files.isDirectory(file))
					continue;

				boolean videoExt = PGUtils.isVideo(file);
				boolean imageExt = PGUtils.isImage(file);

				if (videoExt) {
					Path outputFile = prevDirFile.resolve(file.getFileName().toString() + ".png");
					if (!Files.exists(outputFile))
						createVideoMinature(file, outputFile);
				}

				if (imageExt) {
					Path outputFile = miniDirFile.resolve(file.getFileName().toString());
					if (!Files.exists(outputFile))
						createImageMinature(file, outputFile);
				}
			}
		}
	}

	private void processSlideshowImages(Photogallery photogallery) throws IOException {
		PGConfiguration configuration = loadConfiguration();
		String slideshowDir = configuration.getSlideshowDir();
		Path galleryDir = getGalleryPath(photogallery.getPhotogalleryPath());

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
				Path outputFile = slideshowDirFile.resolve(file.getFileName().toString());

				if (Files.exists(outputFile) || Files.isDirectory(file) || !PGUtils.isImage(file))
					continue;

				eventBus.publish(new PGProcessProgressEvent("Zpracování slideshow " + progress + "/" + total));
				progress++;

				// vytvoř slideshow verzi
				try (InputStream is = Files.newInputStream(file)) {
					BufferedImage image = ImageIO.read(is);
					if (image.getWidth() > 900 || image.getHeight() > 700) {
						try {
							PGUtils.resizeImage(file, outputFile, 900, 700);
						} catch (Exception e) {
							logger.error("Při zpracování slideshow pro '{}' došlo k chybě",
									file.getFileName().toString(), e);
						}
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
		Stream<Path> stream = null;
		try {
			String galleryDir = payloadTO.getGalleryDir();
			Path galleryPath = getGalleryPath(galleryDir);
			stream = Files.list(galleryPath);
			logger.info("modifyPhotogallery thread: " + Thread.currentThread().getId());

			// Počet kroků = miniatury + detaily + uložení
			int total = (int) stream.count();
			eventBus.publish(new PGProcessStartEvent(2 * total + 1));

			Photogallery photogallery = existingId == null ? new Photogallery()
					: photogalleryRepository.findOne(existingId);

			// nasetuj do ní vše potřebné
			photogallery.setPhotogalleryPath(galleryDir);

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

			eventBus.publish(new PGProcessProgressEvent("Uložení obsahu galerie"));

			// vytvoř miniatury
			processMiniatureImages(photogallery);

			// vytvoř detaily
			processSlideshowImages(photogallery);

			eventBus.publish(new PGProcessResultEvent(photogallery.getId()));
		} catch (Exception e) {
			publishPGProcessFailure();
			logger.error("Nezdařilo se uložit galerii", e);
			return;
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	@Override
	public String createGalleryDir() throws IOException {
		PGConfiguration configuration = loadConfiguration();
		String dirRoot = configuration.getRootDir();
		Path dirRootFile = fileSystemService.getFileSystem().getPath(dirRoot);
		long systime = System.currentTimeMillis();
		Path tmpDirFile = dirRootFile.resolve("pgGal_" + systime);
		Files.createDirectories(tmpDirFile);
		return tmpDirFile.getFileName().toString();
	}

	@Override
	public PhotogalleryTO getPhotogalleryForDetail(Long id) {
		Validate.notNull(id, "Id galerie nesmí být null");
		Photogallery photogallery = photogalleryRepository.findOne(id);
		if (photogallery == null)
			return null;
		return photogalleriesMapper.mapPhotogalleryForDetail(photogallery);
	}

	@Override
	public List<PhotogalleryTO> getAllPhotogalleriesForSearch() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	/**
	 * Získá {@link Path} dle jména adresáře galerie
	 * 
	 * @param galleryDir
	 *            jméno adresáře galerie
	 * @return {@link Path} objekt galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	private Path getGalleryPath(String galleryDir) {
		PGConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Kořenový adresář PG modulu musí existovat");
		rootPath = rootPath.normalize();
		Path galleryPath = rootPath.resolve(galleryDir);
		if (!galleryPath.normalize().startsWith(rootPath))
			throw new IllegalArgumentException("Podtečení kořenového adresáře galerií");
		return galleryPath;
	}

	/**
	 * Pokusí se smazat soubor z galerie
	 * 
	 * @param file
	 *            jméno souboru
	 * @param photogalleryTO
	 *            adresář galerie
	 * @param subDir
	 *            jméno subadresáře
	 * @throws IOException
	 */
	private void tryDeleteGalleryFile(String file, Path galleryDir, GalleryFileType fileType) throws IOException {
		Path targetDir = galleryDir;
		switch (fileType) {
		case MAIN_FILE:
			break;
		case MINIATURE:
			targetDir = targetDir.resolve(loadConfiguration().getMiniaturesDir());
			break;
		case PREVIEW:
			targetDir = targetDir.resolve(loadConfiguration().getPreviewsDir());
			break;
		case SLIDESHOW:
			targetDir = targetDir.resolve(loadConfiguration().getSlideshowDir());
			break;
		default:
			break;
		}
		Path subFile = targetDir.resolve(file);
		if (Files.exists(subFile))
			Files.delete(subFile);
	}

	@Override
	public List<PhotogalleryRESTOverviewTO> getAllPhotogalleriesForREST(Long userId, int page, int pageSize) {
		Pageable pageable = new PageRequest(page, pageSize);
		return photogalleriesMapper.mapPhotogalleryForRESTOverviewCollection(userId != null
				? photogalleryRepository.findByUserAccess(userId,pageable) : photogalleryRepository.findByAnonAccess(pageable));
	}

	@Override
	public PhotogalleryRESTTO getPhotogalleryForREST(Long id) throws UnauthorizedAccessException {
		Photogallery gallery = photogalleryRepository.findOne(id);
		if (gallery == null)
			return null;

		UserInfoTO user = securityFacade.getCurrentUser();
		if (gallery.getContentNode().getPublicated() || user.isAdmin()
				|| gallery.getContentNode().getAuthor().getId().equals(user.getId())) {

			PGConfiguration configuration = loadConfiguration();
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
				return new PhotogalleryRESTTO(gallery.getId(), gallery.getContentNode().getName(),
						gallery.getContentNode().getCreationDate(), gallery.getContentNode().getLastModificationDate(),
						gallery.getContentNode().getAuthor().getName(), files);
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

	@Async
	@Override
	public void zipGallery(String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);

		logger.info("zipPhotogallery thread: " + Thread.currentThread().getId());

		final ReferenceHolder<Integer> total = new ReferenceHolder<>();
		final ReferenceHolder<Integer> progress = new ReferenceHolder<>();

		try (Stream<Path> stream = Files.list(galleryPath)) {
			total.setValue((int) stream.count());
			eventBus.publish(new PGZipProcessStartEvent(total.getValue() + 1));
		} catch (Exception e) {
			String msg = "Nezdařilo se získat počet souborů ke komprimaci";
			eventBus.publish(new PGZipProcessResultEvent(msg, e));
			logger.error(msg, e);
			return;
		}

		progress.setValue(1);

		String zipFileName = "grassPGTmpFile-" + new Date().getTime() + "-" + galleryDir + ".zip";
		Path zipFile = galleryPath.resolve(zipFileName);

		try (FileSystem zipFileSystem = fileSystemService.newZipFileSystem(zipFile, true)) {
			performZip(galleryPath, zipFileSystem, progress, total);
			eventBus.publish(new PGZipProcessResultEvent(zipFile));
		} catch (Exception e) {
			String msg = "Nezdařilo se vytvořit ZIP galerie";
			eventBus.publish(new PGZipProcessResultEvent(msg, e));
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
				eventBus.publish(new PGZipProcessProgressEvent("Přidávám '" + src.getFileName() + "' do ZIPu "
						+ progress.getValue() + "/" + total.getValue()));
				progress.setValue(progress.getValue() + 1);

				// Přidávám jenom soubory fotek a videí, miniatury/náhledy a
				// slideshow nechci
				if (!Files.isDirectory(src)) {
					Path dest = root.resolve(src.getFileName().toString());
					Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	@Override
	public List<PhotogalleryViewItemTO> deleteFiles(Set<PhotogalleryViewItemTO> selected, String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);
		List<PhotogalleryViewItemTO> removed = new ArrayList<>();
		for (PhotogalleryViewItemTO itemTO : selected) {
			String file = itemTO.getName();
			try {
				if (PGUtils.isImage(file)) {
					tryDeleteGalleryFile(file, galleryPath, GalleryFileType.MINIATURE);
					tryDeleteGalleryFile(file, galleryPath, GalleryFileType.SLIDESHOW);
				}
				if (PGUtils.isVideo(file))
					tryDeleteGalleryFile(file, galleryPath, GalleryFileType.PREVIEW);
				tryDeleteGalleryFile(file, galleryPath, GalleryFileType.MAIN_FILE);
				removed.add(itemTO);
			} catch (IOException e) {
				logger.error("Nezdařilo se smazat soubor {}", file, e);
			}
		}
		return removed;
	}

	@Override
	public Path getFullImage(String galleryDir, String file) {
		Path galleryPath = getGalleryPath(galleryDir);
		Path filePath = galleryPath.resolve(file);
		if (!filePath.normalize().startsWith(galleryPath))
			throw new IllegalArgumentException("Podtečení adresáře galerie");
		return filePath;
	}

	@Override
	public void uploadFile(InputStream in, String fileName, String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		Path filePath = galleryPath.resolve(fileName);
		if (!filePath.normalize().startsWith(galleryPath))
			throw new IllegalArgumentException("Podtečení adresáře galerie");
		Files.copy(in, filePath);
	}

	@Override
	public List<PhotogalleryViewItemTO> getItems(String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		List<PhotogalleryViewItemTO> items = new ArrayList<>();
		try (Stream<Path> stream = Files.list(galleryPath)) {
			stream.filter(file -> !Files.isDirectory(file)).forEach(file -> {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				itemTO.setName(file.getFileName().toString());
				items.add(itemTO);
			});

		}
		return items;
	}

	@Override
	public int getViewItemsCount(String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		try (Stream<Path> stream = Files.list(galleryPath)) {
			return (int) stream.filter(file -> !Files.isDirectory(file)).count();
		}
	}

	@Override
	public List<PhotogalleryViewItemTO> getViewItems(String galleryDir, int skip, int limit) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		PGConfiguration configuration = loadConfiguration();
		Path miniaturesDir = galleryPath.resolve(configuration.getMiniaturesDir());
		Path previewDir = galleryPath.resolve(configuration.getPreviewsDir());
		List<PhotogalleryViewItemTO> list = new ArrayList<>();
		try (Stream<Path> miniaturesStream = Files.list(miniaturesDir);
				Stream<Path> previewsStream = Files.list(previewDir);) {
			Stream.concat(miniaturesStream, previewsStream).skip(skip).limit(limit).forEach(file -> {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				String fileName = file.getFileName().toString();
				if (file.startsWith(previewDir)) {
					itemTO.setType(PhotogalleryItemType.VIDEO);
					// u videa je potřeba useknout příponu preview obrázku
					// '.png', aby zůstala původní video přípona
					itemTO.setName(fileName.substring(0, fileName.length() - 4));
				} else {
					itemTO.setType(PhotogalleryItemType.IMAGE);
					itemTO.setName(fileName);
				}
				itemTO.setFile(file);
				list.add(itemTO);
			});
		}
		return list;
	}

	@Override
	public PhotogalleryViewItemTO getSlideshowItem(String galleryDir, int index) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		PGConfiguration configuration = loadConfiguration();
		Path miniaturesDir = galleryPath.resolve(configuration.getMiniaturesDir());
		Path previewDir = galleryPath.resolve(configuration.getPreviewsDir());
		Path slideshowDir = galleryPath.resolve(configuration.getSlideshowDir());
		List<PhotogalleryViewItemTO> list = new ArrayList<>();
		try (Stream<Path> miniaturesStream = Files.list(miniaturesDir);
				Stream<Path> previewsStream = Files.list(previewDir);) {
			Stream.concat(miniaturesStream, previewsStream).skip(index).limit(1).forEach(file -> {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				String fileName = file.getFileName().toString();
				if (file.startsWith(previewDir)) {
					itemTO.setType(PhotogalleryItemType.VIDEO);
					// u videa je potřeba useknout příponu preview obrázku
					// '.png', aby zůstala původní video přípona
					itemTO.setName(fileName.substring(0, fileName.length() - 4));
					itemTO.setFile(galleryPath.resolve(itemTO.getName()));
				} else {
					itemTO.setType(PhotogalleryItemType.IMAGE);
					itemTO.setName(fileName);
					itemTO.setFile(slideshowDir.resolve(fileName));
					// možná byl tak malý, že nebylo potřeba vytvářet slideshow
					// velikost a stačí použít přímo původní soubor obrázku
					if (!Files.exists(itemTO.getFile()))
						itemTO.setFile(galleryPath.resolve(fileName));
				}
				list.add(itemTO);
			});
		}
		return list.get(0);
	}

	@Override
	public boolean checkGallery(String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);
		PGConfiguration conf = loadConfiguration();
		return Files.exists(galleryPath) && (Files.exists(galleryPath.resolve(conf.getMiniaturesDir()))
				|| Files.exists(galleryPath.resolve(conf.getPreviewsDir())));
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
	public void deleteDraftGallery(String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		Files.walkFileTree(galleryPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try {
					Files.delete(file);
				} catch (Exception e) {
					logger.error("Nezdařilo se smazat soubor zrušené rozpracované galerie {}",
							file.getFileName().toString(), e);
				}
				return FileVisitResult.CONTINUE;
			}
		});
		Files.delete(galleryPath);
	}
}

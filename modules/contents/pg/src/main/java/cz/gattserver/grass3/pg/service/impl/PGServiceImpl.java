package cz.gattserver.grass3.pg.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
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
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;
import cz.gattserver.grass3.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.pg.util.DecodeAndCaptureFrames;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.pg.util.PhotogalleryMapper;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.SecurityService;

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

	private void deleteFileRecursively(File file) {
		File[] subfiles = file.listFiles();
		if (subfiles != null) {
			for (File subFile : subfiles) {
				deleteFileRecursively(subFile);
			}
		}
		if (file.delete() == false)
			throw new IllegalStateException("Nelze smazat soubor \'" + file.getAbsolutePath() + "\'");
	}

	@Override
	public void deletePhotogallery(PhotogalleryTO photogallery) {
		File galleryDir = getGalleryDir(photogallery);
		deleteFileRecursively(galleryDir);

		photogalleryRepository.delete(photogallery.getId());
		ContentNodeTO contentNodeDTO = photogallery.getContentNode();
		contentNodeFacade.deleteByContentNodeId(contentNodeDTO.getId());
	}

	/**
	 * Vytoří nové miniatury
	 */
	private boolean processMiniatureImages(Photogallery photogallery) {
		PGConfiguration configuration = getConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		String previewsDir = configuration.getPreviewsDir();
		File galleryDir = getGalleryDir(photogallery);

		int total = galleryDir.listFiles().length;
		int progress = 1;

		File miniDirFile = new File(galleryDir, miniaturesDir);
		if (miniDirFile.exists() == false) {
			if (miniDirFile.mkdir() == false)
				return false;
		}

		File prevDirFile = new File(galleryDir, previewsDir);
		if (prevDirFile.exists() == false) {
			if (prevDirFile.mkdir() == false)
				return false;
		}

		for (File file : galleryDir.listFiles()) {

			// pokud bych miniaturizoval adresář nebo miniatura existuje přeskoč
			if (file.isDirectory())
				continue;

			boolean videoExt = PGUtils.isVideo(file.getName());
			boolean imageExt = PGUtils.isImage(file.getName());

			if ((videoExt | imageExt) == false) {
				continue;
			}

			// soubor miniatury
			File outputFile;
			if (videoExt) {
				outputFile = new File(prevDirFile, file.getName() + ".png");
			} else {
				outputFile = new File(miniDirFile, file.getName());
			}

			eventBus.publish(new PGProcessProgressEvent("Zpracování miniatur " + progress + "/" + total));
			progress++;

			if (outputFile.exists())
				continue;

			// vytvoř miniaturu
			if (videoExt) {
				logger.info("Video found");
				try {
					logger.info("Video processing prepared");
					BufferedImage image = new DecodeAndCaptureFrames().decodeAndCaptureFrames(file.getAbsolutePath());
					logger.info("Video processing started");
					image = PGUtils.resizeBufferedImage(image, 150, 150);
					ImageIO.write(image, "png", outputFile);

					logger.info("Video processing finished");
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Video processing failed", e);
				}
			} else {
				try {
					PGUtils.resizeAndRotateImageFile(file, outputFile, 150, 150);
				} catch (java.lang.Exception e) {
					e.printStackTrace();
					if (outputFile.exists())
						outputFile.delete();
					continue;
				}
			}
		}

		return true;
	}

	/**
	 * Vytoří nové slideshow verze souborů
	 */
	private boolean processSlideshowImages(Photogallery photogallery) {
		PGConfiguration configuration = getConfiguration();
		String slideshowDir = configuration.getSlideshowDir();
		File galleryDir = getGalleryDir(photogallery);

		int total = galleryDir.listFiles().length;
		int progress = 1;

		File slideshowDirFile = new File(galleryDir, slideshowDir);
		if (slideshowDirFile.exists() == false) {
			if (slideshowDirFile.mkdir() == false)
				return false;
		}

		for (File file : galleryDir.listFiles()) {

			if (file.isDirectory())
				continue;

			if (PGUtils.isImage(file.getName()) == false)
				continue;

			// soubor slideshow
			File outputFile = new File(slideshowDirFile, file.getName());

			eventBus.publish(new PGProcessProgressEvent("Zpracování slideshow " + progress + "/" + total));
			progress++;

			if (PGUtils.isVideo(file.getName())) {
				continue;
			}

			if (outputFile.exists())
				continue;

			// vytvoř slideshow verzi
			try {
				BufferedImage image = ImageIO.read(file);
				if (image.getWidth() > 900 || image.getHeight() > 700) {
					PGUtils.resizeAndRotateImageFile(file, outputFile, 900, 700);
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();
				if (outputFile.exists())
					outputFile.delete();
				continue;
			}
		}

		return true;
	}

	@Override
	@Async
	public void modifyPhotogallery(String name, Collection<String> tags, boolean publicated,
			PhotogalleryTO photogalleryDTO, String contextRoot, LocalDateTime date) {
		try {
			logger.info("modifyPhotogallery thread: " + Thread.currentThread().getId());

			// Počet kroků = miniatury + detaily + uložení
			eventBus.publish(new PGProcessStartEvent(2 * getGalleryDir(photogalleryDTO).listFiles().length + 1));

			Photogallery photogallery = photogalleryRepository.findOne(photogalleryDTO.getId());

			// vytvoř miniatury
			processMiniatureImages(photogallery);

			// vytvoř detaily
			processSlideshowImages(photogallery);

			// ulož ho
			if (photogalleryRepository.save(photogallery) == null) {
				eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
				return;
			}

			contentNodeFacade.modify(photogalleryDTO.getContentNode().getId(), name, tags, publicated, date);

		} catch (java.lang.Exception e) {
			// content node
			eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
			logger.error("Nezdařilo se uložit galerii", e);
			return;
		}

		eventBus.publish(new PGProcessResultEvent());
	}

	@Override
	public File createGalleryDir() {
		PGConfiguration configuration = getConfiguration();
		String dirRoot = configuration.getRootDir();
		File dirRootFile = new File(dirRoot);

		long systime = System.currentTimeMillis();

		for (int i = 0; i < 10000; i++) {
			File tmpDirFile = new File(dirRootFile, String.valueOf(systime) + "_" + i);
			if (tmpDirFile.mkdirs()) {
				// zdařilo se - vracím jméno tmp adresáře
				return tmpDirFile;
			}
		}

		// ani na 10000 pokusů se nepodařilo vytvořit nekonfliktní adresář
		return null;
	}

	@Override
	@Async
	public void savePhotogallery(String name, Collection<String> tags, File galleryDir, boolean publicated,
			NodeOverviewTO node, UserInfoTO author, String contextRoot, LocalDateTime date) {
		System.out.println("savePhotogallery thread: " + Thread.currentThread().getId());

		// Počet kroků = miniatury + detaily + uložení
		eventBus.publish(new PGProcessStartEvent(2 * galleryDir.listFiles().length + 1));

		// vytvoř novou galerii
		Photogallery photogallery = new Photogallery();

		// nasetuj do ní vše potřebné
		photogallery.setPhotogalleryPath(galleryDir.getName());

		// vytvoř miniatury
		processMiniatureImages(photogallery);

		// vytvoř detaily
		processSlideshowImages(photogallery);

		// ulož ho a nasetuj jeho id
		photogallery = photogalleryRepository.save(photogallery);
		if (photogallery == null) {
			eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
			return;
		}

		// vytvoř odpovídající content node
		eventBus.publish(new PGProcessProgressEvent("Uložení obsahu galerie"));
		Long contentNodeId = contentNodeFacade.save(PGModule.ID, photogallery.getId(), name, tags,
				publicated, node.getId(), author.getId(), false, date, null);

		// ulož do galerie referenci na její contentnode
		ContentNode contentNode = new ContentNode();
		contentNode.setId(contentNodeId);
		photogallery.setContentNode(contentNode);
		if (photogalleryRepository.save(photogallery) == null) {
			eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
			return;
		}

		eventBus.publish(new PGProcessResultEvent(photogallery.getId()));
	}

	@Override
	public PhotogalleryTO getPhotogalleryForDetail(Long id) {
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
	public File getGalleryDir(PhotogalleryTO photogallery) {
		return new File(getConfiguration().getRootDir(), photogallery.getPhotogalleryPath());
	}

	private File getGalleryDir(Photogallery photogallery) {
		return new File(getConfiguration().getRootDir(), photogallery.getPhotogalleryPath());
	}

	@Override
	public void tryDeleteMiniatureImage(File file, PhotogalleryTO photogalleryDTO) {
		PGConfiguration configuration = getConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		File galleryDir = getGalleryDir(photogalleryDTO);
		File miniDirFile = new File(galleryDir, miniaturesDir);

		File miniFile = new File(miniDirFile, file.getName());
		if (miniFile.exists())
			miniFile.delete();
	}

	@Override
	public void tryDeletePreviewImage(File file, PhotogalleryTO photogalleryDTO) {
		PGConfiguration configuration = getConfiguration();
		String previewDir = configuration.getPreviewsDir();
		File galleryDir = getGalleryDir(photogalleryDTO);
		File previewDirFile = new File(galleryDir, previewDir);

		File previewFile = new File(previewDirFile, file.getName());
		if (previewFile.exists())
			previewFile.delete();
	}

	@Override
	public void tryDeleteSlideshowImage(File file, PhotogalleryTO photogalleryDTO) {
		PGConfiguration configuration = getConfiguration();
		String slideshowDir = configuration.getSlideshowDir();
		File galleryDir = getGalleryDir(photogalleryDTO);
		File slideshowDirFile = new File(galleryDir, slideshowDir);

		File slideshowFile = new File(slideshowDirFile, file.getName());
		if (slideshowFile.exists())
			slideshowFile.delete();
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
			File file = new File(configuration.getRootDir() + "/" + gallery.getPhotogalleryPath());
			Set<String> files = new HashSet<>();
			if (file.exists()) {
				for (File f : file.listFiles()) {
					if (f.isFile())
						files.add(f.getName());
				}
			} else {
				return null;
			}

			PhotogalleryRESTTO photogalleryDTO = new PhotogalleryRESTTO(gallery.getId(),
					gallery.getContentNode().getName(), gallery.getContentNode().getCreationDate(),
					gallery.getContentNode().getLastModificationDate(), gallery.getContentNode().getAuthor().getName(),
					files);
			return photogalleryDTO;
		}

		throw new UnauthorizedAccessException();
	}

	@Override
	public File getPhotoForREST(Long id, String fileName, boolean mini) throws UnauthorizedAccessException {
		Photogallery gallery = photogalleryRepository.findOne(id);
		if (gallery == null)
			return null;

		UserInfoTO user = securityFacade.getCurrentUser();
		if (gallery.getContentNode().getPublicated() || user.isAdmin()
				|| gallery.getContentNode().getAuthor().getId().equals(user.getId())) {
			PGConfiguration configuration = getConfiguration();
			File file = new File(configuration.getRootDir() + "/" + gallery.getPhotogalleryPath() + "/"
					+ (mini ? configuration.getMiniaturesDir() : configuration.getSlideshowDir()) + "/" + fileName);
			if (file.exists()) {
				return file;
			} else {
				if (!mini) {
					// pokud jsem nenašel slideshow, je to tak malý obrázek, že
					// postačí originální velikost a nemá vytvořený slideshow
					file = new File(configuration.getRootDir() + "/" + gallery.getPhotogalleryPath() + "/" + fileName);
					if (file.exists())
						return file;
				}
			}
			return null;
		}

		throw new UnauthorizedAccessException();
	}

	@Async
	@Override
	public void zipGallery(File galleryDir) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		try {
			logger.info("zipPhotogallery thread: " + Thread.currentThread().getId());

			// Počet kroků
			long total = Files.list(Paths.get(galleryDir.getAbsolutePath())).count();
			int progress = 1;
			eventBus.publish(new PGZipProcessStartEvent((int) total + 1));

			File tmpFile = File.createTempFile("grassPGTmpFile_" + new Date().getTime() + "_" + galleryDir.getName(),
					null);

			fos = new FileOutputStream(tmpFile);
			zipOut = new ZipOutputStream(fos);
			for (File fileToZip : galleryDir.listFiles()) {
				if (!fileToZip.isDirectory()) {
					eventBus.publish(new PGZipProcessProgressEvent(
							"Přidávám '" + fileToZip.getName() + "' do ZIPu " + progress + "/" + total));
					progress++;
					fis = new FileInputStream(fileToZip);
					ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
					zipOut.putNextEntry(zipEntry);

					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zipOut.write(bytes, 0, length);
					}
					fis.close();
					fis = null;
				}
			}
			zipOut.close();
			zipOut = null;
			fos.close();
			fos = null;

			eventBus.publish(new PGZipProcessResultEvent(tmpFile));

		} catch (Exception e) {
			eventBus.publish(new PGZipProcessResultEvent(false, "Nezdařilo se vytvořit ZIP galerie"));
			logger.error("Nezdařilo se vytvořit ZIP galerie", e);
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
				if (zipOut != null)
					zipOut.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			return;
		}
	}

}

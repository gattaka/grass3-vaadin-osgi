package cz.gattserver.grass3.pg.facade;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.config.IConfigurationService;
import cz.gattserver.grass3.events.IEventBus;
import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import cz.gattserver.grass3.pg.dao.PhotoGalleryRepository;
import cz.gattserver.grass3.pg.domain.Photogallery;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.events.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.PGProcessStartEvent;
import cz.gattserver.grass3.pg.service.impl.PhotogalleryContentService;
import cz.gattserver.grass3.pg.util.DecodeAndCaptureFrames;
import cz.gattserver.grass3.pg.util.PGUtils;
import cz.gattserver.grass3.pg.util.PhotogalleryMapper;

@Transactional
@Component("photogalleryFacade")
public class PhotogalleryFacadeImpl implements IPhotogalleryFacade {

	private static Logger logger = LoggerFactory.getLogger(PhotogalleryFacadeImpl.class);

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "photogalleryMapper")
	private PhotogalleryMapper photogalleriesMapper;

	@Resource
	private IConfigurationService configurationService;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Autowired
	private PhotoGalleryRepository photogalleryRepository;

	@Autowired
	private IEventBus eventBus;

	@Override
	public PhotogalleryConfiguration getConfiguration() {
		PhotogalleryConfiguration configuration = new PhotogalleryConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(PhotogalleryConfiguration configuration) {
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
	public void deletePhotogallery(PhotogalleryDTO photogallery) {
		File galleryDir = getGalleryDir(photogallery);
		deleteFileRecursively(galleryDir);

		photogalleryRepository.delete(photogallery.getId());
		ContentNodeDTO contentNodeDTO = photogallery.getContentNode();
		contentNodeFacade.delete(contentNodeDTO.getId());
	}

	/**
	 * Vytoří nové miniatury
	 */
	private boolean processMiniatureImages(Photogallery photogallery) {

		PhotogalleryConfiguration configuration = getConfiguration();
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

		PhotogalleryConfiguration configuration = getConfiguration();
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
				PGUtils.resizeAndRotateImageFile(file, outputFile, 900, 700);
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
			PhotogalleryDTO photogalleryDTO, String contextRoot, Date date) {

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

		PhotogalleryConfiguration configuration = getConfiguration();
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
			NodeDTO category, UserInfoDTO author, String contextRoot, Date date) {

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
		ContentNode contentNode = contentNodeFacade.save(PhotogalleryContentService.ID, photogallery.getId(), name,
				tags, publicated, category.getId(), author.getId(), date);

		if (contentNode == null) {
			eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
			return;
		}

		// ulož do galerie referenci na její contentnode
		photogallery.setContentNode(contentNode);
		if (photogalleryRepository.save(photogallery) == null) {
			eventBus.publish(new PGProcessResultEvent(false, "Nezdařilo se uložit galerii"));
			return;
		}

		eventBus.publish(new PGProcessResultEvent(photogallery.getId()));
	}

	@Override
	public PhotogalleryDTO getPhotogalleryForDetail(Long id) {
		Photogallery photogallery = photogalleryRepository.findOne(id);
		if (photogallery == null)
			return null;
		PhotogalleryDTO photogalleryDTO = photogalleriesMapper.mapPhotogalleryForDetail(photogallery);
		return photogalleryDTO;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForOverview() {
		List<Photogallery> photogalleries = photogalleryRepository.findAll();
		if (photogalleries == null)
			return null;
		List<PhotogalleryDTO> photogalleryDTOs = photogalleriesMapper.mapPhotogalleriesForOverview(photogalleries);
		return photogalleryDTOs;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getGalleryDir(PhotogalleryDTO photogallery) {
		return new File(getConfiguration().getRootDir(), photogallery.getPhotogalleryPath());
	}

	private File getGalleryDir(Photogallery photogallery) {
		return new File(getConfiguration().getRootDir(), photogallery.getPhotogalleryPath());
	}

	@Override
	public void tryDeleteMiniatureImage(File file, PhotogalleryDTO photogalleryDTO) {

		PhotogalleryConfiguration configuration = getConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		File galleryDir = getGalleryDir(photogalleryDTO);
		File miniDirFile = new File(galleryDir, miniaturesDir);

		File miniFile = new File(miniDirFile, file.getName());
		if (miniFile.exists())
			miniFile.delete();
	}

	@Override
	public void tryDeletePreviewImage(File file, PhotogalleryDTO photogalleryDTO) {

		PhotogalleryConfiguration configuration = getConfiguration();
		String previewDir = configuration.getPreviewsDir();
		File galleryDir = getGalleryDir(photogalleryDTO);
		File previewDirFile = new File(galleryDir, previewDir);

		File previewFile = new File(previewDirFile, file.getName());
		if (previewFile.exists())
			previewFile.delete();
		
	}
	
	@Override
	public void tryDeleteSlideshowImage(File file, PhotogalleryDTO photogalleryDTO) {

		PhotogalleryConfiguration configuration = getConfiguration();
		String slideshowDir = configuration.getSlideshowDir();
		File galleryDir = getGalleryDir(photogalleryDTO);
		File slideshowDirFile = new File(galleryDir, slideshowDir);

		File slideshowFile = new File(slideshowDirFile, file.getName());
		if (slideshowFile.exists())
			slideshowFile.delete();
	}

}

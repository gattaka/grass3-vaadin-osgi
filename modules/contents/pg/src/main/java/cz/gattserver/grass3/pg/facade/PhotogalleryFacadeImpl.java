package cz.gattserver.grass3.pg.facade;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.config.IConfigurationService;
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
import cz.gattserver.grass3.pg.service.impl.PhotogalleryContentService;
import cz.gattserver.grass3.pg.util.ImageUtils;
import cz.gattserver.grass3.pg.util.PhotogalleryMapper;

@Transactional
@Component("photogalleryFacade")
public class PhotogalleryFacadeImpl implements IPhotogalleryFacade {

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

	private boolean deleteFileRecursively(File file) {
		File[] subfiles = file.listFiles();
		if (subfiles != null) {
			for (File subFile : subfiles) {
				if (deleteFileRecursively(subFile) == false)
					return false;
			}
		}
		return file.delete();
	}

	@Override
	public boolean deletePhotogallery(PhotogalleryDTO photogallery) {

		File galleryDir = getGalleryDir(photogallery);
		if (deleteFileRecursively(galleryDir) == false)
			return false;

		photogalleryRepository.delete(photogallery.getId());
		ContentNodeDTO contentNodeDTO = photogallery.getContentNode();
		if (contentNodeFacade.delete(contentNodeDTO) == false)
			return false;
		return true;
	}

	/**
	 * Vytoří nové miniatury
	 */
	private boolean processMiniatureImages(Photogallery photogallery) {

		PhotogalleryConfiguration configuration = getConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		File galleryDir = getGalleryDir(photogallery);

		File miniDirFile = new File(galleryDir, miniaturesDir);
		if (miniDirFile.exists() == false) {
			if (miniDirFile.mkdir() == false)
				return false;
		}

		for (File file : galleryDir.listFiles()) {

			// soubor miniatury
			File miniFile = new File(miniDirFile, file.getName());

			// pokud bych miniaturizoval adresář nebo miniatura existuje přeskoč
			if (file.isDirectory() || miniFile.exists())
				continue;

			// vytvoř miniaturu
			try {
				ImageUtils.resizeImageFile(file, miniFile, 150, 150);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
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

		File slideshowDirFile = new File(galleryDir, slideshowDir);
		if (slideshowDirFile.exists() == false) {
			if (slideshowDirFile.mkdir() == false)
				return false;
		}

		for (File file : galleryDir.listFiles()) {

			// soubor slideshow
			File slideshowFile = new File(slideshowDirFile, file.getName());

			if (file.isDirectory() || slideshowFile.exists())
				continue;

			// vytvoř slideshow verzi
			try {
				ImageUtils.resizeImageFile(file, slideshowFile, 900, 700);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean modifyPhotogallery(String name, Collection<String> tags, boolean publicated,
			PhotogalleryDTO photogalleryDTO, String contextRoot) {

		Photogallery photogallery = photogalleryRepository.findOne(photogalleryDTO.getId());

		// vytvoř miniatury
		processMiniatureImages(photogallery);

		// vytvoř detaily
		processSlideshowImages(photogallery);

		// ulož ho
		if (photogalleryRepository.save(photogallery) == null)
			return false;

		// content node
		if (contentNodeFacade.modify(photogalleryDTO.getContentNode(), name, tags, publicated) == false)
			return false;

		return true;
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
	public Long savePhotogallery(String name, Collection<String> tags, File galleryDir, boolean publicated,
			NodeDTO category, UserInfoDTO author, String contextRoot) {

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
		if (photogallery == null)
			return null;

		// vytvoř odpovídající content node
		ContentNodeDTO contentNodeDTO = contentNodeFacade.save(PhotogalleryContentService.ID, photogallery.getId(),
				name, tags, publicated, category, author);

		if (contentNodeDTO == null)
			return null;

		// ulož do galrie referenci na její contentnode
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeDTO.getId());

		photogallery.setContentNode(contentNode);
		if (photogalleryRepository.save(photogallery) == null)
			return null;

		return photogallery.getId();
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

package org.myftp.gattserver.grass3.pg.facade;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentNodeFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeRepository;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pg.dao.PhotoGalleryRepository;
import org.myftp.gattserver.grass3.pg.domain.Photogallery;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;
import org.myftp.gattserver.grass3.pg.service.impl.PhotogalleryContentService;
import org.myftp.gattserver.grass3.pg.util.PhotogalleryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("photogalleryFacade")
public class PhotogalleryFacadeImpl implements IPhotogalleryFacade {

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "photogalleryMapper")
	private PhotogalleryMapper photogalleriesMapper;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Autowired
	private PhotoGalleryRepository photogalleryRepository;

	@Override
	public boolean deletePhotogallery(PhotogalleryDTO photogallery) {
		photogalleryRepository.delete(photogallery.getId());
		ContentNodeDTO contentNodeDTO = photogallery.getContentNode();
		if (contentNodeFacade.delete(contentNodeDTO) == false)
			return false;
		return true;
	}

	@Override
	public boolean modifyPhotogallery(String name, Collection<String> tags,
			boolean publicated, PhotogalleryDTO photogalleryDTO,
			String contextRoot) {

		Photogallery photogallery = photogalleryRepository
				.findOne(photogalleryDTO.getId());

		// nasetuj do něj vše potřebné
		photogallery.setPhotogalleryPath(photogalleryDTO.getPhotogalleryPath());

		// ulož ho
		if (photogalleryRepository.save(photogallery) == null)
			return false;

		// content node
		if (contentNodeFacade.modify(photogalleryDTO.getContentNode(), name,
				tags, publicated) == false)
			return false;

		return true;
	}

	@Override
	public Long savePhotogallery(String name, String photogalleryPath,
			Collection<String> tags, boolean publicated, NodeDTO category,
			UserInfoDTO author, String contextRoot) {

		// vytvoř novou galerii
		Photogallery photogallery = new Photogallery();

		// nasetuj do ní vše potřebné
		photogallery.setPhotogalleryPath(photogalleryPath);

		// ulož ho a nasetuj jeho id
		photogallery = photogalleryRepository.save(photogallery);
		if (photogallery == null)
			return null;

		// vytvoř odpovídající content node
		ContentNodeDTO contentNodeDTO = contentNodeFacade.save(
				PhotogalleryContentService.ID, photogallery.getId(), name,
				tags, publicated, category, author);

		if (contentNodeDTO == null)
			return null;

		// ulož do článku referenci na jeho contentnode
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeDTO
				.getId());

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
		PhotogalleryDTO photogalleryDTO = photogalleriesMapper
				.mapPhotogalleryForDetail(photogallery);
		return photogalleryDTO;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForOverview() {
		List<Photogallery> photogalleries = photogalleryRepository.findAll();
		if (photogalleries == null)
			return null;
		List<PhotogalleryDTO> photogalleryDTOs = photogalleriesMapper
				.mapPhotogalleriesForOverview(photogalleries);
		return photogalleryDTOs;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForSearch() {
		// TODO Auto-generated method stub
		return null;
	}

}

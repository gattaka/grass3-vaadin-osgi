package cz.gattserver.grass3.pg.util;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.model.util.Mapper;
import cz.gattserver.grass3.pg.domain.Photogallery;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;

@Component("photogalleryMapper")
public class PhotogalleryMapper {

	/**
	 * Core mapper
	 */
	@Resource(name = "mapper")
	private Mapper mapper;

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryDTO}
	 */
	public PhotogalleryDTO mapPhotogalleryForDetail(Photogallery photogallery) {
		PhotogalleryDTO photogalleryDTO = new PhotogalleryDTO();

		photogalleryDTO.setId(photogallery.getId());
		photogalleryDTO.setPhotogalleryPath(photogallery.getPhotogalleryPath());

		photogalleryDTO.setContentNode(mapper.mapContentNodeForDetail(photogallery.getContentNode()));
		return photogalleryDTO;
	}

}

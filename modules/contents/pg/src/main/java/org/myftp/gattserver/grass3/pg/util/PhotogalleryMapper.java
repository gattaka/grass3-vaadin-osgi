package org.myftp.gattserver.grass3.pg.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pg.domain.Photogallery;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.stereotype.Component;

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

		photogalleryDTO.setContentNode(mapper
				.mapContentNodeForDetail(photogallery.getContentNode()));
		return photogalleryDTO;
	}

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryDTO} určený pro
	 * přehled
	 */
	public PhotogalleryDTO mapPhotogalleryForOverview(Photogallery photogallery) {
		PhotogalleryDTO photogalleryDTO = new PhotogalleryDTO();
		photogalleryDTO.setContentNode(mapper
				.mapContentNodeForOverview(photogallery.getContentNode()));
		photogalleryDTO.setId(photogallery.getId());
		return photogalleryDTO;
	}

	/**
	 * Převede kolekci {@link Photogallery} na kolekci {@link PhotogalleryDTO}
	 * 
	 * @param photogalleries
	 *            vstupní kolekce entit {@link Photogallery}
	 * @return
	 */
	public List<PhotogalleryDTO> mapPhotogalleriesForOverview(
			List<Photogallery> photogalleries) {
		List<PhotogalleryDTO> photogalleryDTOs = new ArrayList<PhotogalleryDTO>();
		for (Photogallery photogallery : photogalleries) {
			photogalleryDTOs.add(mapPhotogalleryForOverview(photogallery));
		}
		return photogalleryDTOs;
	}

}

package cz.gattserver.grass3.pg.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.model.util.Mapper;
import cz.gattserver.grass3.pg.domain.Photogallery;
import cz.gattserver.grass3.pg.dto.PhotogalleryDTO;
import cz.gattserver.grass3.pg.dto.PhotogalleryRESTOverviewDTO;

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

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryRESTOverviewDTO}
	 */
	public PhotogalleryRESTOverviewDTO mapPhotogalleryForRESTOverview(Photogallery photogallery) {
		PhotogalleryRESTOverviewDTO photogalleryDTO = new PhotogalleryRESTOverviewDTO();

		photogalleryDTO.setId(photogallery.getId());
		photogalleryDTO.setName(photogallery.getContentNode().getName());

		return photogalleryDTO;
	}

	/**
	 * Převede list {@link Photogallery} na list
	 * {@link PhotogalleryRESTOverviewDTO}
	 */
	public List<PhotogalleryRESTOverviewDTO> mapPhotogalleryForRESTOverviewCollection(
			List<Photogallery> photogalleryCollection) {
		List<PhotogalleryRESTOverviewDTO> list = new ArrayList<>();

		for (Photogallery photogallery : photogalleryCollection) {
			PhotogalleryRESTOverviewDTO to = mapPhotogalleryForRESTOverview(photogallery);
			list.add(to);
		}

		return list;
	}

}

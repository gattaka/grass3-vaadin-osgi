package cz.gattserver.grass3.pg.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.model.util.CoreMapper;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;

@Component
public class PhotogalleryMapperImpl implements PhotogalleryMapper {

	/**
	 * Core mapper
	 */
	@Autowired
	private CoreMapper mapper;

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryTO}
	 */
	public PhotogalleryTO mapPhotogalleryForDetail(Photogallery photogallery) {
		if (photogallery == null)
			return null;

		PhotogalleryTO photogalleryDTO = new PhotogalleryTO();
		photogalleryDTO.setId(photogallery.getId());
		photogalleryDTO.setPhotogalleryPath(photogallery.getPhotogalleryPath());
		photogalleryDTO.setContentNode(mapper.mapContentNodeForDetail(photogallery.getContentNode()));
		return photogalleryDTO;
	}

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryRESTOverviewTO}
	 */
	public PhotogalleryRESTOverviewTO mapPhotogalleryForRESTOverview(Photogallery photogallery) {
		if (photogallery == null)
			return null;

		PhotogalleryRESTOverviewTO photogalleryDTO = new PhotogalleryRESTOverviewTO();
		photogalleryDTO.setId(photogallery.getId());
		photogalleryDTO.setName(photogallery.getContentNode().getName());
		return photogalleryDTO;
	}

	/**
	 * Převede list {@link Photogallery} na list
	 * {@link PhotogalleryRESTOverviewTO}
	 */
	public List<PhotogalleryRESTOverviewTO> mapPhotogalleryForRESTOverviewCollection(
			List<Photogallery> photogalleryCollection) {
		List<PhotogalleryRESTOverviewTO> list = new ArrayList<>();
		for (Photogallery photogallery : photogalleryCollection) {
			PhotogalleryRESTOverviewTO to = mapPhotogalleryForRESTOverview(photogallery);
			list.add(to);
		}
		return list;
	}

}

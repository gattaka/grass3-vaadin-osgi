package cz.gattserver.grass3.pg.util;

import java.util.List;

import cz.gattserver.grass3.pg.interfaces.PhotogalleryDTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewDTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;

public interface PhotogalleryMapper {

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryDTO}
	 */
	public PhotogalleryDTO mapPhotogalleryForDetail(Photogallery photogallery);

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryRESTOverviewDTO}
	 */
	public PhotogalleryRESTOverviewDTO mapPhotogalleryForRESTOverview(Photogallery photogallery);

	/**
	 * Převede list {@link Photogallery} na list
	 * {@link PhotogalleryRESTOverviewDTO}
	 */
	public List<PhotogalleryRESTOverviewDTO> mapPhotogalleryForRESTOverviewCollection(
			List<Photogallery> photogalleryCollection);

}

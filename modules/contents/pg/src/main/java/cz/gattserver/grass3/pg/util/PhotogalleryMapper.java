package cz.gattserver.grass3.pg.util;

import java.util.List;

import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.model.domain.Photogallery;

public interface PhotogalleryMapper {

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryTO}
	 */
	public PhotogalleryTO mapPhotogalleryForDetail(Photogallery photogallery);

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryRESTOverviewTO}
	 */
	public PhotogalleryRESTOverviewTO mapPhotogalleryForRESTOverview(Photogallery photogallery);

	/**
	 * Převede list {@link Photogallery} na list
	 * {@link PhotogalleryRESTOverviewTO}
	 */
	public List<PhotogalleryRESTOverviewTO> mapPhotogalleryForRESTOverviewCollection(
			List<Photogallery> photogalleryCollection);

}

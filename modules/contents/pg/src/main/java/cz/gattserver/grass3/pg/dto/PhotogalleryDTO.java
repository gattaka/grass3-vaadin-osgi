package cz.gattserver.grass3.pg.dto;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;

public class PhotogalleryDTO {

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeDTO contentNode;

	/**
	 * Relativní cesta (od kořene fotogalerie) k adresáři s fotografiemi
	 */
	private String photogalleryPath;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNodeDTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeDTO contentNode) {
		this.contentNode = contentNode;
	}

	public String getPhotogalleryPath() {
		return photogalleryPath;
	}

	public void setPhotogalleryPath(String photogalleryPath) {
		this.photogalleryPath = photogalleryPath;
	}

}

package cz.gattserver.grass3.model.dto;

import java.time.LocalDateTime;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami, overview
 * 
 * @author gatt
 * 
 */
public class ContentNodeOverviewDTO {

	/**
	 * ID služby, která daný obsah umí číst
	 */
	private String contentReaderID;

	/**
	 * ID samotného obsahu v rámci dané služby (typu obsahu)
	 */
	private Long contentID;

	/**
	 * Název obsahu
	 */
	private String name;

	/**
	 * nadřazený uzel (kategorie ve které obsah je)
	 */
	private NodeOverviewDTO parent;

	/**
	 * Kdy byl obsah vytvořen
	 */
	private LocalDateTime creationDate;

	/**
	 * Kdy byl naposledy upraven
	 */
	private LocalDateTime lastModificationDate;

	/**
	 * Je obsah ve fázi příprav, nebo už má být publikován ?
	 */
	private Boolean publicated = true;

	/**
	 * Kdo ho vytvořil
	 */
	private UserInfoDTO author;

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

	public String getContentReaderID() {
		return contentReaderID;
	}

	public void setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(LocalDateTime lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public Boolean isPublicated() {
		return publicated;
	}

	public void setPublicated(Boolean publicated) {
		this.publicated = publicated;
	}

	public Long getContentID() {
		return contentID;
	}

	public void setContentID(Long contentID) {
		this.contentID = contentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeOverviewDTO getParent() {
		return parent;
	}

	public void setParent(NodeOverviewDTO parent) {
		this.parent = parent;
	}

	public UserInfoDTO getAuthor() {
		return author;
	}

	public void setAuthor(UserInfoDTO author) {
		this.author = author;
	}

}

package org.myftp.gattserver.grass3.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class ContentNodeDTO implements Serializable {

	private static final long serialVersionUID = 4852850012419713481L;

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
	 * Kdy byl obsah vytvořen
	 */
	private Date creationDate;

	/**
	 * Kdy byl naposledy upraven
	 */
	private Date lastModificationDate;

	/**
	 * Tagy
	 */
	private Set<ContentTagDTO> contentTags;

	/**
	 * Autor
	 */
	private UserDTO author;

	/**
	 * nadřazený uzel (kategorie ve které obsah je)
	 */
	private Long parentID;

	/**
	 * DB identifikátor
	 */
	private Long Id;

	/**
	 * Je obsah ve fázi příprav, nebo už má být publikován ?
	 */
	private Boolean publicated = true;

	public ContentNodeDTO() {
	}

	public ContentNodeDTO(String contentReaderID, Long contentID) {
		this.contentID = contentID;
		this.contentReaderID = contentReaderID;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getParentID() {
		return parentID;
	}

	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getPublicated() {
		return publicated;
	}

	public void setPublicated(Boolean publicated) {
		this.publicated = publicated;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public String getContentReaderID() {
		return contentReaderID;
	}

	public void setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
	}

	public Long getContentID() {
		return contentID;
	}

	public void setContentID(Long contentID) {
		this.contentID = contentID;
	}

	public UserDTO getAuthor() {
		return author;
	}

	public void setAuthor(UserDTO author) {
		this.author = author;
	}

	public Set<ContentTagDTO> getContentTags() {
		return contentTags;
	}

	public void setContentTags(Set<ContentTagDTO> contentTags) {
		this.contentTags = contentTags;
	}

	@Override
	public int hashCode() {
		return Id == null ? 0 : Id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ContentNodeDTO that = (ContentNodeDTO) obj;
		return that.getId() == Id;
	}

}

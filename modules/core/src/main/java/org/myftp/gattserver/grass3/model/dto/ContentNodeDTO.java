package org.myftp.gattserver.grass3.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentNodeDTO implements Serializable{

	private static final long serialVersionUID = 723375154665160018L;

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
	private NodeDTO parent;

	/**
	 * Kdy byl obsah vytvořen
	 */
	private Date creationDate;

	/**
	 * Kdy byl naposledy upraven
	 */
	private Date lastModificationDate;

	/**
	 * Je obsah ve fázi příprav, nebo už má být publikován ?
	 */
	private Boolean publicated = true;

	/**
	 * Tagy
	 */
	private Set<ContentTagDTO> contentTags;

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

	public NodeDTO getParent() {
		return parent;
	}

	public void setParent(NodeDTO parent) {
		this.parent = parent;
	}

	public UserInfoDTO getAuthor() {
		return author;
	}

	public void setAuthor(UserInfoDTO author) {
		this.author = author;
	}

	public Set<ContentTagDTO> getContentTags() {
		return contentTags;
	}

	public void setContentTags(Set<ContentTagDTO> contentTags) {
		this.contentTags = contentTags;
	}

}

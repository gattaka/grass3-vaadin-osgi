package org.myftp.gattserver.grass3.model.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTENTNODE")
public class ContentNode {

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
	private Long parentID;

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
	private List<ContentTag> contentTags;

	/**
	 * Kdo ho vytvořil
	 */
	private User author;

	/**
	 * DB identifikátor
	 */
	private Long id;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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

	public Boolean getPublicated() {
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

	@OneToMany
	public List<ContentTag> getContentTags() {
		return contentTags;
	}

	public void setContentTags(List<ContentTag> contentTags) {
		this.contentTags = contentTags;
	}

	@ManyToOne
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Column(name = "parent_id")
	public Long getParentID() {
		return parentID;
	}

	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}

}
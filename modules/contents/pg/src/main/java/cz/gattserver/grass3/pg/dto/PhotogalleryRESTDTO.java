package cz.gattserver.grass3.pg.dto;

import java.util.Date;
import java.util.Set;

public class PhotogalleryRESTDTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název
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
	 * Jméno uživatele
	 */
	private String author;

	/**
	 * Jména souborů fotek
	 */
	private Set<String> files;

	public PhotogalleryRESTDTO(Long id, String name, Date creationDate, Date lastModificationDate, String author,
			Set<String> files) {
		super();
		this.id = id;
		this.name = name;
		this.creationDate = creationDate;
		this.lastModificationDate = lastModificationDate;
		this.author = author;
		this.files = files;
	}

	public Set<String> getFiles() {
		return files;
	}

	public void setFiles(Set<String> files) {
		this.files = files;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

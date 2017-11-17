package cz.gattserver.grass3.interfaces;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentNodeTO implements Serializable, Authorizable {

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
	private NodeOverviewTO parent;

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
	 * Jde o plnohodnotný článek, nebo jde o rozpracovaný obsah?
	 */
	private Boolean draft = false;

	/**
	 * Jde-li o draft upravovaného obsahu, jaké je jeho id
	 */
	private Long draftSourceId;

	/**
	 * Tagy
	 */
	private Set<ContentTagOverviewTO> contentTags;

	/**
	 * Kdo ho vytvořil
	 */
	private UserInfoTO author;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getDraftSourceId() {
		return draftSourceId;
	}

	public void setDraftSourceId(Long draftSourceId) {
		this.draftSourceId = draftSourceId;
	}

	public Boolean isDraft() {
		return draft == null ? false : draft;
	}

	public void setDraft(Boolean draft) {
		this.draft = draft;
	}

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

	public NodeOverviewTO getParent() {
		return parent;
	}

	public void setParent(NodeOverviewTO parent) {
		this.parent = parent;
	}

	public UserInfoTO getAuthor() {
		return author;
	}

	public void setAuthor(UserInfoTO author) {
		this.author = author;
	}

	public Set<ContentTagOverviewTO> getContentTags() {
		return contentTags;
	}

	public void setContentTags(Set<ContentTagOverviewTO> contentTags) {
		this.contentTags = contentTags;
	}

}
